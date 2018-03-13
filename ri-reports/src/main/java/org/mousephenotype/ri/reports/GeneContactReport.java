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

package org.mousephenotype.ri.reports;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.ri.core.DateUtils;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.GeneContactReportRow;
import org.mousephenotype.ri.reports.support.MpCSVWriter;
import org.mousephenotype.ri.reports.support.ReportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.inject.Inject;
import java.beans.Introspector;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;


@SpringBootApplication
public class GeneContactReport extends AbstractReport implements CommandLineRunner {

    private Logger    logger     = LoggerFactory.getLogger(this.getClass());
    private String    reportName = ClassUtils.getShortClassName(this.getClass());
    private DateUtils dateUtils  = new DateUtils();
    private SqlUtils  sqlUtils;

    @Inject
    public GeneContactReport(SqlUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(this.getClass().getSuperclass().getSimpleName());
    }


    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(GeneContactReport.class);
        app.setWebEnvironment(false);               // Inhibits launching of Tomcat.
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if (!errors.isEmpty()) {
            logger.error(reportName + " parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        List<String> headerParams = Arrays.asList(new String[]{
                "contact_email", "contact_active_state", "contact_created_at", "marker_symbol", "mgi_accession_id", "gene_interest_created_at"});

        csvWriter.writeRow(headerParams);

        // Get the data
        List<GeneContactReportRow> geneContacts = sqlUtils.getGeneContactReportRow();

        // Write the data.
        final String     NO_DATA = "--";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:.mm:ss");
        for (GeneContactReportRow geneContactReportRow : geneContacts) {

            List<String> row = Arrays.asList(new String[]{
                      geneContactReportRow.getContactEmail()
                    , Integer.toString(geneContactReportRow.getContactActiveState())
                    , (geneContactReportRow.getContactCreatedAt() != null ? fmt.format(geneContactReportRow.getContactCreatedAt()) : NO_DATA)
                    , geneContactReportRow.getMarkerSymbol()
                    , geneContactReportRow.getMgiAccessionId()
                    , (geneContactReportRow.getGeneInterestCreatedDate() != null ? fmt.format(geneContactReportRow.getGeneInterestCreatedDate()) : NO_DATA)

            });

            csvWriter.writeRow(row);
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        logger.info(String.format("Finished. [%s]", dateUtils.msToHms(System.currentTimeMillis() - start)));
    }


    protected void initialise(String[] args) throws ReportException {
        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            for (String error : errors) {
                System.out.println(error);
            }
            System.out.println();
            usage();
            System.exit(1);
        }

        if (parser.showHelp()) {
            usage();
            System.exit(0);
        }

        if (parser.getReportFormat() != null) {
            this.reportFormat = parser.getReportFormat();
        }
        this.targetFilename =
                parser.getPrefix()
                        + (parser.getTargetFilename() != null ? parser.getTargetFilename() : getDefaultFilename())
                        + "."
                        + reportFormat;

        this.targetFile = new File(Paths.get(parser.getTargetDirectory(), targetFilename).toAbsolutePath().toString());
        try {
            FileWriter fileWriter = new FileWriter(targetFile.getAbsoluteFile());
            this.csvWriter = new MpCSVWriter(fileWriter, reportFormat.getSeparator());
        } catch (IOException e) {
            throw new ReportException("Exception opening FileWriter: " + e.getLocalizedMessage());
        }

        logInputParameters();
    }
}