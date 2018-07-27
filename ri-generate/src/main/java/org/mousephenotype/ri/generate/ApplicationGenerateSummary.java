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

package org.mousephenotype.ri.generate;

import org.mousephenotype.ri.core.utils.SqlUtils;
import org.mousephenotype.ri.core.entities.Gene;
import org.mousephenotype.ri.core.entities.GeneStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Created by mrelac on 05/06/2017.
 *
 * This class is intended to be a command-line callable java main program that generates a single e-mail to each
 * contact with a list of genes for which they have registered interest, and the current status of each such gene.
 */
@Deprecated
// FIXME
public class ApplicationGenerateSummary implements CommandLineRunner {

    private Map<Integer, List<Gene>> genesByContactMap;
    private final Logger             logger = LoggerFactory.getLogger(this.getClass());
    private SqlUtils                 sqlUtils;
    private List<String>             headings = Arrays.asList(new String[]{
            "Gene Symbol", "Gene MGI Accession Id", "Assignment Status", "Null Allele Production", "Conditional Allele Production", "Phenotyping Data Available", "Action"
    });

    private final String             mailto = "mouse-helpdesk@ebi.ac.uk";
    private final String             subject = "Complete list of IMPC genes for which you have registered interest";
    private Map<Integer, GeneStatus> geneStatusMap;     // key is gene_status.pk

    @Inject
    public ApplicationGenerateSummary(SqlUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }


    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ApplicationGenerateSummary.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }


    @Override
    /**
     * Supported arguments:
     *   -- welcome emailAddress        # Generates and sends a single welcome e-mail to the specified email address
     *   -- summary emailAddress        # Generates and sends a single summary e-mail to the specified email address
     *   -- summary                     # Generates and sends a summary to each registered user
     */
    public void run(String... args) throws Exception {




//
//        int count = 0;
//        String message;
//
//        genesByContactMap = sqlUtils.getGenesByContactPk();
//        geneStatusMap = sqlUtils.getStatusMapByStatusPk();
//
//        /**
//         * Truncate the gene_sent_summary table.
//         * For each contact:
//         *      - Create a GeneSentSummary instance for the contact
//         *      - Get the list of genes for which this user is registered
//         *      - For each gene :
//         *          - Append the gene and status to the body
//         *      - Set the GeneSentSummary 'sent at' field to null to indicate the  e-mail has not been set
//         *      - Write the GeneSentSummary object to the gene_sent_summary table.
//         * Log a single record that the summary was generated
//         */
//
//        sqlUtils.truncateGeneSentSummary();
//
//        for (Map.Entry<Integer, List<Gene>> entry : genesByContactMap.entrySet()) {
//
//            int contactPk = entry.getKey();
//            List<Gene> genes = entry.getValue();
//
//            GeneSentSummary summary = new GeneSentSummary();
//            summary.setSubject(subject);
//            summary.setBody(buildBody(genes));
//            summary.setContactPk(contactPk);
//            summary.setCreatedAt(new Date());
//            summary.setSentAt(null);
//
//            int geneSentSummaryPk = sqlUtils.insertGeneSentSummary(summary);
//
//            message = "ri-generate: ApplicationGenerateSummary: contactPk::geneSentSummaryPk -> " +
//                    contactPk + "::" + geneSentSummaryPk;
//
//            count++;
//        }
//
//        logger.info("Run ApplicationGenerateSummary: " + count + " emails queued for gene status changes");
//    }
//
//
//    // PRIVATE METHODS
//
//    private final String style =
//            "  table {"+
//            "    font-family: arial, sans-serif;"+
//            "    border-collapse: collapse;"+
//            "    width: 100%;"+
//            "}"+
//            "td, th {"+
//            "    border: 1px solid #dddddd;"+
//            "    text-align: left;"+
//            "    padding: 8px;"+
//            "}"+
//            "tr:nth-child(even) {"+
//            "    background-color: #dddddd;"+
//            "}";
//    private String buildBody(List<Gene> genes) {
//
//        StringBuilder body = new StringBuilder();
//
//        String gdpr_link = "https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices";
//        String gdpr_p1 = "You have previously actively joined the IMPC \"Register Interest\" list, which records your email address and genes for which you would like updates on mouse knockout production and phenotyping.";
//        String gdpr_p2 = "You can unsubscribe from any gene for which you have registered interest by clicking on its corresponding unregister link in the Action column below.";
//        String gdpr_p3 = "You can read more about how the IMPC will use your personal data by looking a the IMPC privacy policy here:";
//        String gdpr_p4 = buildHtmlCell("div", gdpr_link, gdpr_link);
//
//        body
//                .append("<html>")
//                .append("Dear colleague,")
//                .append("<br />")
//                .append("<br />")
//                .append("Below please find a summary of the IMPC genes for which you have registered interest.")
//                .append("<br />")
//                .append("<br />")
//
//                .append(gdpr_p1)
//                .append("<br />")
//                .append("<br />")
//
//                .append(gdpr_p2)
//                .append("<br />")
//                .append("<br />")
//
//                .append(gdpr_p3)
//                .append("<br />")
//                .append("<br />")
//
//                .append(gdpr_p4)
//                .append("<br />")
//                .append("<br />")
//
//                .append("<style>" + style + "</style>")
//                .append("<table id=\"genesTable\">")
//                .append(buildRow("th", headings));
//        for (Gene gene : genes) {
//            body.append(buildRow(gene));
//        }
//        body
//                .append("</table>")
//                .append("<br />")
//                .append(getEpilogue())
//                .append("</html>");
//
//        return body.toString();
//    }
//
//    private String buildRow(Gene gene) {
//
//        StringBuilder row = new StringBuilder();
//        String anchor;
//        String cell;
//        String value;
//
//        GeneStatus geneStatus;
//
//        row.append("<tr>");
//
//        // Gene symbol
//        anchor = "http://www.mousephenotype.org/data/genes/" + gene.getMgiAccessionId();
//        cell = buildHtmlCell("td", gene.getSymbol(), anchor);
//        row.append(cell);
//
//        // Gene MGI accession id
//        anchor = "http://www.informatics.jax.org/marker/" + gene.getMgiAccessionId();
//        cell = buildHtmlCell("td", gene.getMgiAccessionId(), anchor);
//        row.append(cell);
//
//        // Assignment status
//        geneStatus = geneStatusMap.get(gene.getRiAssignmentStatusPk());
//
//        if ((geneStatus != null) && (geneStatus.getStatus().equalsIgnoreCase(GeneStatus.PRODUCTION_AND_PHENOTYPING_PLANNED))){
//            value = "Selected for production and phenotyping";
//        } else if ((geneStatus != null) && (geneStatus.getStatus().equalsIgnoreCase(GeneStatus.WITHDRAWN))){
//            value = "Withdrawn";
//        } else {
//            value = "Not planned";
//        }
//        cell = buildHtmlCell("td", value, null);
//        row.append(cell);
//
//        // Null allele production
//        geneStatus = geneStatusMap.get(gene.getRiNullAlleleProductionStatusPk());
//
//        if ((geneStatus != null) && (geneStatus.getStatus().equalsIgnoreCase(GeneStatus.MOUSE_PRODUCTION_STARTED))){
//            value = "Started";
//            anchor = null;
//        } else if ((geneStatus != null) && (geneStatus.getStatus().equalsIgnoreCase(GeneStatus.MOUSE_PRODUCED))){
//            value = "Genotype confirmed mice";
//            anchor = "http://www.mousephenotype.org/data/search/allele2?kw=\"" + gene.getMgiAccessionId() + "\"";
//        } else {
//            value = "None";
//            anchor = null;
//        }
//        cell = buildHtmlCell("td", value, anchor);
//        row.append(cell);
//
//        // Conditional allele production
//        geneStatus = geneStatusMap.get(gene.getRiConditionalAlleleProductionStatusPk());
//
//        if ((geneStatus != null) && (geneStatus.getStatus().equalsIgnoreCase(GeneStatus.MOUSE_PRODUCTION_STARTED))){
//            value = "Started";
//            anchor = null;
//        } else if ((geneStatus != null) && (geneStatus.getStatus().equalsIgnoreCase(GeneStatus.MOUSE_PRODUCED))){
//            value = "Genotype confirmed mice";
//            anchor = "http://www.mousephenotype.org/data/search/allele2?kw=\"" + gene.getMgiAccessionId() + "\"";
//        } else {
//            value = "None";
//            anchor = null;
//        }
//        cell = buildHtmlCell("td", value, anchor);
//        row.append(cell);
//
//        // Phenotyping data available
//        geneStatus = geneStatusMap.get((gene.getRiPhenotypingStatusPk() == null ? null : gene.getRiPhenotypingStatusPk()));
//        if ((geneStatus != null) && (geneStatus.getStatus().equalsIgnoreCase(GeneStatus.PHENOTYPING_DATA_AVAILABLE))) {
//            value = "Yes";
//            anchor = "http://www.mousephenotype.org/data/genes/" + gene.getMgiAccessionId() + "#section-associations";
//        } else {
//            value = "No";
//            anchor = null;
//        }
//        cell = buildHtmlCell("td", value, anchor);
//        row.append(cell);
//
//
//        // Action
//        anchor = "https://www.mousephenotype.org/toggleflagfromjs/" + gene.getMgiAccessionId();
//        value = "Unregister";
//        cell = buildHtmlCell("td", value, anchor);
//        row.append(cell);
//
//        row.append("</tr>");
//
//        return row.toString();
//    }
//
//    private String buildRow(String tag, List<String> values) {
//        StringBuffer row = new StringBuffer();
//
//        row.append("<tr>");
//        for (String value : values) {
//            String escapedValue = HtmlUtils.htmlEscape(value);
//            row.append("<" + tag + ">" + escapedValue + "</" + tag + ">");
//        }
//        row.append("</tr>");
//
//        return row.toString();
//    }
//
//    private String buildHtmlCell(String tag, String value, String anchor) {
//
//        StringBuilder sb = new StringBuilder();
//
//        String escapedValue = HtmlUtils.htmlEscape(value);
//        String escapedAnchor = (anchor == null ? null : HtmlUtils.htmlEscape(anchor));
//
//        sb.append("<" + tag + ">");
//        if (escapedAnchor != null) {
//            sb.append("<a href=\"" + escapedAnchor + "\" alt =\"" + escapedAnchor + "\">");
//        }
//        sb.append(escapedValue);
//        if (escapedAnchor != null) {
//            sb.append("</a>");
//        }
//        sb.append("</" + tag + ">");
//
//        return sb.toString();
//    }
//
//    private String getEpilogue() {
//        StringBuilder body = new StringBuilder();
//
//        body
//                .append("For further information / enquiries please write to:")
//                .append("<a href=\"mailto: " + mailto + "\">" + mailto + "</a>.<br />")
//                .append("<br />")
//                .append("Best Regards,<br />")
//                .append("<br />")
//                .append("The MPI2 (KOMP2) informatics consortium");
//
//        return body.toString();
    }
}