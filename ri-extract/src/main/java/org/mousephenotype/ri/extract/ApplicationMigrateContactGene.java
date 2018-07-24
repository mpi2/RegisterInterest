/*******************************************************************************
 *  Copyright © 2017 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.ri.extract;

import org.mousephenotype.ri.core.DateUtils;
import org.mousephenotype.ri.core.SecurityUtils;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.Gene;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class implements the migration and extraction of the ContactGene report from iMits.
 *
 * Created by mrelac on 19/07/2017.
 */
@EnableBatchProcessing
@ComponentScan({"org.mousephenotype.ri.extract"})
public class ApplicationMigrateContactGene implements CommandLineRunner {

    @NotNull
    @Value("${download.workspace}")
    protected String downloadWorkspace;

    @NotNull
    @Value("${ContactGeneUrl}")
    protected String sourceUrl;

    private DateUtils dateUtils = new DateUtils();
    private Logger logger      = LoggerFactory.getLogger(this.getClass());
    private String targetFilename;
    private SqlUtils sqlUtils;

    public static final int COL_MGI_ACCESSION_ID                     = 0;
    public static final int COL_MARKER_SYMBOL                        = 1;
    public static final int COL_EMAIL                                = 2;
    public static final int COL_GENE_CONTACT_CREATED_AT              = 3;
    public static final int COL_CONTACT_CREATED_AT                   = 4;

    SecurityUtils securityUtils = new SecurityUtils();


    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ApplicationMigrateContactGene.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }


    @Inject
    public ApplicationMigrateContactGene(SqlUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }


    @Override
    public void run(String... args) throws Exception {
        targetFilename = downloadWorkspace + "/ContactGene.tsv";
        long start;

        try {

            start = new Date().getTime();
            download();
            logger.info("Downloaded " + sourceUrl + " to " + targetFilename + " in " + dateUtils.msToHms(new Date().getTime() - start));

        } catch (InterestException e) {

            logger.warn(e.getLocalizedMessage());
        }

        try {

            start = new Date().getTime();
            int count = migrateContactGenes();
            logger.info("Extracted " + count + " contact-gene associations in " + dateUtils.msToHms(new Date().getTime() - start));

        } catch (InterestException e) {

            logger.warn(e.getLocalizedMessage());
        }
    }

    public void download() throws InterestException {

        FileOutputStream fos;
        ReadableByteChannel rbc;
        final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
        String outputAppender = DATE_FORMAT.format(new Date());
        String source;
        String target;
        String targetTemp;
        URL url;

        try

        {
            Files.createDirectories(Paths.get(targetFilename).getParent());

        } catch (IOException e) {

            logger.error("Create download directory '" + targetFilename + "' failed. Reason: " + e.getLocalizedMessage());
        }

        target = targetFilename;
        targetTemp = target + "." + outputAppender;
        source = sourceUrl;
        try

        {
            url = new URL(source);
            rbc = Channels.newChannel(url.openStream());
            fos = new FileOutputStream(targetTemp);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            Files.move(Paths.get(targetTemp), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {

            String message = "Download of " + source + " -> " + target + " failed. Reason: Bad URL: " + e.getLocalizedMessage();
            throw new InterestException(message);
        }
    }


    @Transactional
    public int migrateContactGenes() throws InterestException {

        int               count                  = 0;
        String            line;
        String[]          parts;
        Map<String, Gene> genesByGeneAccessionId = sqlUtils.getGenesByGeneAccessionId();

        // The imits report that fees this method contains all registered contact/gene pairs, so there's no need
        // to compute unregistered associations - simply remove all contact_gene and contact rows, then add.
        sqlUtils.deleteAllContactGenes();
        sqlUtils.deleteAllContactRoles();
        sqlUtils.deleteAllContacts();

        try {
            BufferedReader br = new BufferedReader(new FileReader(targetFilename));
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty())                     // Skip the newline. Thank you, java.
                    continue;

                if (lineNumber++ == 1)
                    continue;                           // Skip the heading.

                parts = line.split(Pattern.quote("\t"));

                if (parts.length != 5) {

                    logger.error("FATAL ERROR: Input file '{}' contains {} fields. Expected 5.", targetFilename, parts.length);

                    return count;
                }

                String geneAccessionId = parts[COL_MGI_ACCESSION_ID];
                String emailAddress = parts[COL_EMAIL];

                // Get the gene. It is an error if the gene does not exist.
                Gene gene = genesByGeneAccessionId.get(geneAccessionId);
                if (gene == null) {

                    logger.error("ERROR: createAccount for contact '{}' failed because gene accession id {} doesn't exist.", emailAddress, geneAccessionId);
                    continue;
                }

                // Create the contact and register the gene association to the contact.
                sqlUtils.createAccount(emailAddress, securityUtils.generateSecureRandomPassword());
                sqlUtils.registerGene(emailAddress, gene.getMgiAccessionId());
                count++;
            }

            if (br != null) {
                br.close();
            }

        } catch (IOException e) {

            String message = "Open file '" + targetFilename + "' failed. Reason: " + e.getLocalizedMessage();
            throw new InterestException(message);
        }

        return count;
    }


    // PRIVATE METHODS


    private Date parseDate(String dateString) throws InterestException {

        Date date = dateUtils.convertToDate(dateString);
        
        if (date == null) {
            throw new InterestException( "Invalid date: '" + dateString + "'");
        }
        
        return date;
    }
}