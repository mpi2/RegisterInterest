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
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.ContactGene;
import org.mousephenotype.ri.core.entities.GeneSent;
import org.mousephenotype.ri.core.entities.ImitsStatus;
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
 *
 * 2018-07-24 (mrelac) NOTE: This class is no longer needed, as its purpose was to capture the last e-mail that was sent.
 *                           The last e-mail sent by RegisterInterest was the 25 May GDPR summary, and no RI jobs have
 *                           run since then that generate e-mails.
 */
@EnableBatchProcessing
@ComponentScan({"org.mousephenotype.ri.extract"})
@Deprecated
public class ApplicationMigrateContactGeneSent implements CommandLineRunner {

    @NotNull
    @Value("${download.workspace}")
    protected String downloadWorkspace;

    @NotNull
    @Value("${ContactGeneSentUrl}")
    protected String sourceUrl;


    private DateUtils dateUtils = new DateUtils();
    private Logger logger      = LoggerFactory.getLogger(this.getClass());
    private String targetFilename;
    private Map<String, ImitsStatus> imitsStatusMap;
    private SqlUtils sqlUtils;

    public static final int COL_MGI_ACCESSION_ID                     = 0;
    public static final int COL_MARKER_SYMBOL                        = 1;
    public static final int COL_EMAIL                                = 2;
    public static final int COL_LAST_EMAIL_SENT_DATE                 = 3;
    public static final int COL_GENE_ASSIGNMENT_STATUS               = 4;
    public static final int COL_CONDITIONAL_ALLELE_PRODUCTION_STATUS = 5;
    public static final int COL_NULL_ALLELE_PRODUCTION_STATUS        = 6;
    public static final int COL_PHENOTYPING_STATUS                   = 7;
    public static final int COL_GENE_CONTACT_CREATED_AT              = 8;
    public static final int COL_CONTACT_CREATED_AT                   = 9;



    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ApplicationMigrateContactGeneSent.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }


    @Inject
    public ApplicationMigrateContactGeneSent(SqlUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }


    @Override
    public void run(String... args) throws Exception {
        targetFilename = downloadWorkspace + "/ContactGeneSent.tsv";
        imitsStatusMap = sqlUtils.getImitsStatusMap();
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
            int count = extract();
            logger.info("Extracted " + count + " records in " + dateUtils.msToHms(new Date().getTime() - start));

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
    public int extract() throws InterestException {

        int count = 0;
        String line;
        String[] parts;

        try {
            BufferedReader br = new BufferedReader(new FileReader(targetFilename));
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty())                     // Skip the newline. Thank you, java.
                    continue;

                if (lineNumber++ == 1)
                    continue;                           // Skip the heading.

                parts = line.split(Pattern.quote("\t"));

                if (parts.length != 10) {
                    logger.error("Input file '" + targetFilename + "' contains " + parts.length + " fields. Expected 10.");
                    return count;
                }

                String mgiAccessionId = parts[COL_MGI_ACCESSION_ID];
                String email = parts[COL_EMAIL];
                String contactCreatedAtString = parts[COL_CONTACT_CREATED_AT];
                String contactGeneCreatedAtString = parts[COL_GENE_CONTACT_CREATED_AT];
                String assignmentStatusString = parts[COL_GENE_ASSIGNMENT_STATUS];
                String conditionalAlleleProductionStatusString = parts[COL_CONDITIONAL_ALLELE_PRODUCTION_STATUS];
                String nullAlleleProductionStatusString = parts[COL_NULL_ALLELE_PRODUCTION_STATUS];
                String phenotypingStatusString = parts[COL_PHENOTYPING_STATUS];
                String sentAtString = parts[COL_LAST_EMAIL_SENT_DATE];

                Date contactCreatedAt = parseDate(contactCreatedAtString);
                Date contactGeneCreatedAt = parseDate(contactGeneCreatedAtString);
                Integer assignmentStatusPk = null;
                if ((assignmentStatusString != null) && ( ! assignmentStatusString.trim().isEmpty())) {
                    assignmentStatusPk = getStatusPk(assignmentStatusString);
                }
                Integer conditionalAlleleProductionStatusPk = null;
                if ((conditionalAlleleProductionStatusString != null) && ( ! conditionalAlleleProductionStatusString.trim().isEmpty())) {
                    conditionalAlleleProductionStatusPk = getStatusPk(conditionalAlleleProductionStatusString);
                }
                Integer nullAlleleProductionStatusPk = null;
                if ((nullAlleleProductionStatusString != null) && ( ! nullAlleleProductionStatusString.trim().isEmpty())) {
                    nullAlleleProductionStatusPk = getStatusPk(nullAlleleProductionStatusString);
                }
                Integer phenotypingStatusPk = null;
                if ((phenotypingStatusString != null) && ( ! phenotypingStatusString.trim().isEmpty())) {
                    phenotypingStatusPk = getStatusPk(phenotypingStatusString);
                }

                Date sentAt = parseDate(sentAtString);

                ContactGene contactGene;
                GeneSent geneSent = new GeneSent();

                try {

                    int contactGenePk = sqlUtils.insertOrUpdateInterestGene("migrator", mgiAccessionId, email);

                    geneSent.setSubject("migrated");
                    geneSent.setBody("migrated");
                    geneSent.setContactGenePk(contactGenePk);
                    geneSent.setAssignmentStatusPk(assignmentStatusPk);
                    geneSent.setConditionalAlleleProductionStatusPk(conditionalAlleleProductionStatusPk);
                    geneSent.setNullAlleleProductionStatusPk(nullAlleleProductionStatusPk);
                    geneSent.setPhenotypingStatusPk(phenotypingStatusPk);
                    geneSent.setCreatedAt(new Date());
                    geneSent.setSentAt(sentAt);

                    sqlUtils.insertGeneSent(geneSent);
                    count++;

                } catch (InterestException e) {

                    logger.error("insertGeneSent for '" + email + "' failed. Reason: " + e.getLocalizedMessage());
                    continue;
                }
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


    private Integer getStatusPk(String imitsStatusString) throws InterestException {

        ImitsStatus imitsStatus = imitsStatusMap.get(imitsStatusString);
        if (imitsStatus == null) {
            throw new InterestException("Invalid iMits status '" + imitsStatusString + "'");
        }

        return imitsStatus.getGeneStatusPk();
    }

    private Date parseDate(String dateString) throws InterestException {

        Date date = dateUtils.convertToDate(dateString);

        if (date == null) {
            throw new InterestException( "Invalid date: '" + dateString + "'");
        }

        return date;
    }
}