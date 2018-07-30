/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.ri.core.entities;

import org.mousephenotype.ri.core.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SummaryHtmlTable {

    private final  Logger                   logger   = LoggerFactory.getLogger(this.getClass());
    private static List<String>             headings = Arrays.asList(new String[] {
            "Gene Symbol", "Gene MGI Accession Id", "Assignment Status", "Null Allele Production", "Conditional Allele Production", "Phenotyping Data Available", "Action"
    });


    public static final String style =
            "  table {"+
            "    font-family: arial, sans-serif;"+
            "    border-collapse: collapse;"+
            "    width: 100%;"+
            "}"+
            "td, th {"+
            "    border: 1px solid #dddddd;"+
            "    text-align: left;"+
            "    padding: 8px;"+
            "}"+
            "tr:nth-child(even) {"+
            "    background-color: #dddddd;"+
            "}";

    public static String buildTableContent(String paBaseUrl, SqlUtils sqlUtils, Summary summary, boolean showChangedGenes) {

        Map<String, GeneSent> genesSentByGeneAccessionid = sqlUtils.getGeneSentStatusByEmailAddress(summary.getEmailAddress());
        StringBuilder body = new StringBuilder();

        body
                .append("<style>" + style + "</style>")
                .append("<table id=\"genesTable\">")
                .append(buildRow("th", headings));

        for (Gene gene : summary.getGenes()) {
            body.append(buildRow(paBaseUrl, gene, genesSentByGeneAccessionid.get(gene.getMgiAccessionId()), showChangedGenes));
        }

        body.append("</table>");

        return body.toString();
    }


    /**
     * Builds an html data row for the specified gene and optional {@link GeneSent} instance. {@code} may be null.
     * @param gene This contact's {@link Gene} instance. Never null.
     * @param geneSent This contact's {@link GeneSent} instance. May be null.
     * @param showChangedGenes Boolean indicating whether or not to indicate the gene(s) whose status has changed since
     *                         the last e-mail went out
     * @return html tr text, wrapped in tr tag.
     */
    public static String buildRow(String paBaseUrl, Gene gene, GeneSent geneSent, boolean showChangedGenes) {

        StringBuilder row = new StringBuilder();
        String anchor;
        String cell;
        String currentValue;
        String lastSentValue;

        GeneStatus geneStatus;

        row.append("<tr>");

        // Gene symbol
        anchor = paBaseUrl + "/genes/" + gene.getMgiAccessionId();
        cell = buildHtmlCell("td", gene.getSymbol(), anchor);
        row.append(cell);


        // Gene MGI accession id
        anchor = "http://www.informatics.jax.org/marker/" + gene.getMgiAccessionId();
        cell = buildHtmlCell("td", gene.getMgiAccessionId(), anchor);
        row.append(cell);


        // Assignment status
        currentValue = gene.getRiAssignmentStatus() == null ? "Not planned" : gene.getRiAssignmentStatus();
        lastSentValue = (geneSent.getAssignmentStatus() == null ? "Not planned" : geneSent.getAssignmentStatus());

        if (showChangedGenes) {
            // Indicate if status has changed since last e-mail was sent.
            if ( ! currentValue.equals(lastSentValue)) {
                currentValue += " *";
            }
        }
        cell = buildHtmlCell("td", currentValue, null);
        row.append(cell);


        // Null allele production
        currentValue = gene.getRiNullAlleleProductionStatus() == null ? "None" : gene.getRiNullAlleleProductionStatus();
        lastSentValue = (geneSent.getNullAlleleProductionStatus() == null ? "None" : geneSent.getNullAlleleProductionStatus());

        if (currentValue.equals(GeneStatus.MOUSE_PRODUCTION_STARTED)){
            anchor = null;
        } else if (currentValue.equals(GeneStatus.MOUSE_PRODUCED)) {
            anchor = paBaseUrl + "/search/allele2?k2=\"" + gene.getMgiAccessionId() + "\"";
        } else {
            anchor = null;
        }
        if (showChangedGenes) {
            // Indicate if status has changed since last e-mail was sent.
            if ( ! currentValue.equals(lastSentValue)) {
                currentValue += " *";
            }
        }
        cell = buildHtmlCell("td", currentValue, anchor);
        row.append(cell);


        // Conditional allele production
        currentValue = gene.getRiConditionalAlleleProductionStatus() == null ? "None" : gene.getRiConditionalAlleleProductionStatus();
        lastSentValue = (geneSent.getConditionalAlleleProductionStatus() == null ? "None" : geneSent.getConditionalAlleleProductionStatus());

        if (currentValue.equals(GeneStatus.MOUSE_PRODUCTION_STARTED)) {
            anchor = null;
        } else if (currentValue.equals(GeneStatus.MOUSE_PRODUCED)) {
            anchor = paBaseUrl + "/search/allele2?k2=\"" + gene.getMgiAccessionId() + "\"";
        } else {
            anchor = null;
        }
        if (showChangedGenes) {
            if ( ! currentValue.equals(lastSentValue)) {
                currentValue += " *";
            }
        }
        cell = buildHtmlCell("td", currentValue, anchor);
        row.append(cell);


        // Phenotyping data available
        currentValue = gene.getRiPhenotypingStatus() == null ? "No" : gene.getRiPhenotypingStatus();
        lastSentValue = (geneSent.getPhenotypingStatus() == null ? "No" : geneSent.getPhenotypingStatus());

        if (currentValue.equals(GeneStatus.PHENOTYPING_DATA_AVAILABLE)) {
            currentValue = "Yes";
            anchor = paBaseUrl + "/genes/" + gene.getMgiAccessionId() + "#section-associations";
        } else {
            currentValue = "No";
            anchor = null;
        }
        if (lastSentValue.equals(GeneStatus.PHENOTYPING_DATA_AVAILABLE)) {
            lastSentValue = "Yes";
        } else {
            lastSentValue = "No";
        }
        if (showChangedGenes) {
            if ( ! currentValue.equals(lastSentValue)) {
                currentValue += " *";
            }
        }
        cell = buildHtmlCell("td", currentValue, anchor);
        row.append(cell);


        // Action
        anchor = paBaseUrl + "/unregistration/gene?geneAccessionId=" + gene.getMgiAccessionId();
        currentValue = "Unregister";
        cell = buildHtmlCell("td", currentValue, anchor);
        row.append(cell);

        row.append("</tr>");

        return row.toString();
    }

    public static String buildRow(String tag, List<String> values) {
        StringBuffer row = new StringBuffer();

        row.append("<tr>");
        for (String value : values) {
            String escapedValue = HtmlUtils.htmlEscape(value);
            row.append("<" + tag + ">" + escapedValue + "</" + tag + ">");
        }
        row.append("</tr>");

        return row.toString();
    }

    public static String buildHtmlCell(String tag, String value, String anchor) {

        StringBuilder sb = new StringBuilder();

        String escapedValue = HtmlUtils.htmlEscape(value);
        String escapedAnchor = (anchor == null ? null : HtmlUtils.htmlEscape(anchor));

        sb.append("<" + tag + ">");
        if (escapedAnchor != null) {
            sb.append("<a href=\"" + escapedAnchor + "\" alt =\"" + escapedAnchor + "\">");
        }
        sb.append(escapedValue);
        if (escapedAnchor != null) {
            sb.append("</a>");
        }
        sb.append("</" + tag + ">");

        return sb.toString();
    }
}