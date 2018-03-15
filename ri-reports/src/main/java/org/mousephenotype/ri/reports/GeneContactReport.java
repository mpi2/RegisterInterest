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

import java.beans.Introspector;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;


public class GeneContactReport extends AbstractReport implements CommandLineRunner {

    private Logger    logger     = LoggerFactory.getLogger(this.getClass());
    private String    reportName = ClassUtils.getShortClassName(this.getClass());
    private DateUtils dateUtils  = new DateUtils();
    private SqlUtils  sqlUtils;

    public GeneContactReport(SqlUtils sqlUtils, MpCSVWriter csvWriter)
    {
        this.sqlUtils = sqlUtils;
        this.csvWriter = csvWriter;
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(this.getClass().getSuperclass().getSimpleName());
    }


    public static void main(String[] args) {
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

        long start = System.currentTimeMillis();

        List<String> headerParams = Arrays.asList(
                "contact_email", "contact_active_state", "contact_created_at", "marker_symbol", "mgi_accession_id", "gene_interest_created_at");

        csvWriter.writeRow(headerParams);

        // Get the data
        List<GeneContactReportRow> geneContacts = sqlUtils.getGeneContactReportRow();

        // Write the data.
        final String     NO_DATA = "--";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:.mm:ss");
        for (GeneContactReportRow geneContactReportRow : geneContacts) {

            List<String> row = Arrays.asList(
                      geneContactReportRow.getContactEmail()
                    , Integer.toString(geneContactReportRow.getContactActiveState())
                    , (geneContactReportRow.getContactCreatedAt() != null ? fmt.format(geneContactReportRow.getContactCreatedAt()) : NO_DATA)
                    , geneContactReportRow.getMarkerSymbol()
                    , geneContactReportRow.getMgiAccessionId()
                    , (geneContactReportRow.getGeneInterestCreatedDate() != null ? fmt.format(geneContactReportRow.getGeneInterestCreatedDate()) : NO_DATA)

            );

            csvWriter.writeRow(row);
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        logger.info(String.format("Finished. [%s]", dateUtils.msToHms(System.currentTimeMillis() - start)));
    }
}