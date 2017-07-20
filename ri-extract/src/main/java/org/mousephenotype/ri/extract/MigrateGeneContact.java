package org.mousephenotype.ri.extract;

import org.mousephenotype.ri.core.DateUtils;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.Contact;
import org.mousephenotype.ri.core.entities.Gene;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

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
 * This class implements the migration and extraction of the GeneContact report from iMits.
 *
 * Created by mrelac on 19/07/2017.
 */
@EnableBatchProcessing
@ComponentScan({"org.mousephenotype.ri.extract"})
public class MigrateGeneContact implements CommandLineRunner {

    @NotNull
    @Value("${download.workspace}")
    protected String downloadWorkspace;

    @NotNull
    @Value("${GeneContact}")
    protected String geneContact;

    @Autowired
    private SqlUtils sqlUtils;

    private DateUtils dateUtils = new DateUtils();
    private Logger logger      = LoggerFactory.getLogger(this.getClass());

    private String sourceUrl = geneContact;
    private String targetFilename;


    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(MigrateGeneContact.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }


    @Override
    public void run(String... args) throws Exception {
        targetFilename = downloadWorkspace + "/GeneContact.tsv";
        long start;

        try {

            start = new Date().getTime();
            download();
            logger.info("Ddownloaded " + sourceUrl + " to " + targetFilename + " in " + dateUtils.msToHms(new Date().getTime() - start));

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
        Map<String, Gene> genesMap = sqlUtils.getGenes();


        try {
            BufferedReader br = new BufferedReader(new FileReader(targetFilename));
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty())                     // Skip the newline. Thank you, java.
                    continue;

                if (lineNumber++ == 1)
                    continue;                           // Skip the heading.

                parts = line.split(Pattern.quote("\t"));

                if (parts.length != 4) {
                    logger.error(" Input file '" + targetFilename + "' contains " + parts.length + " fields. Expected 4.");
                    return count;
                }

                String mgiAccessionId = parts[0];
                String email = parts[1];
                String contactCreatedAtString = parts[2];
                String geneContactCreatedAtString = parts[3];

                Date contactCreatedAt = parseDate(contactCreatedAtString);
                Date geneContactCreatedAt = parseDate(geneContactCreatedAtString);

                try {

                    Gene gene = genesMap.get(mgiAccessionId);
                    Contact contact = sqlUtils.updateOrInsertContact("migrator", email, 1, contactCreatedAt);
                    int localCount = sqlUtils.insertOrUpdateGeneContact(gene.getPk(), contact.getPk(), geneContactCreatedAt);
                    count += localCount;

                } catch (InterestException e) {

                    logger.error("insertContact for '" + email + "' failed. Reason: " + e.getLocalizedMessage());
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


    private Date parseDate(String dateString) throws InterestException {

        Date date = dateUtils.convertToDate(dateString);
        
        if (date == null) {
            throw new InterestException( "Invalid date: '" + dateString + "'");
        }
        
        return date;
    }
}