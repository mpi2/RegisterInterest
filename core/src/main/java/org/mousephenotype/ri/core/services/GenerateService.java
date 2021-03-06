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

package org.mousephenotype.ri.core.services;

import org.mousephenotype.ri.core.entities.GeneSent;
import org.mousephenotype.ri.core.entities.Summary;
import org.mousephenotype.ri.core.entities.SummaryHtmlTable;
import org.mousephenotype.ri.core.entities.SummaryWithDecoration;
import org.mousephenotype.ri.core.utils.SqlUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

/**
 * This class exports the services offered by the generate module.
 */
@Service
public class GenerateService {

    public static final String PRIVACY_POLICY_LINK = "https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices";
    public static final String MOUSEINFORMATICS_EMAIL = "mouse-helpdesk@ebi.ac.uk";


    private String   paBaseUrl;
    private String   riBaseUrl;
    private SqlUtils sqlUtils;

    @Inject
    public GenerateService(String paBaseUrl, String riBaseUrl, SqlUtils sqlUtils) {
        this.paBaseUrl = paBaseUrl;
        this.riBaseUrl = riBaseUrl;
        this.sqlUtils = sqlUtils;
    }


    /**
     *
     * @return A map of all registered contact summaries, indexed by contact email address
     */
    Map<String, Summary> getAllSummariesByEmailAddress() {

        return sqlUtils.getAllSummariesByEmailAddress();
    }


    public Map<String, GeneSent> getGeneSentStatusByGeneAccessionId(String emailAddress) {

        return sqlUtils.getGeneSentStatusByGeneAccessionId(emailAddress);
    }

    /**
     *
     * @return A map of all registered contact summaries, indexed by contact email address
     */
    public Summary getsummaryByEmailAddress(String emailAddress) {

        return sqlUtils.getSummary(emailAddress);
    }


    /**
     * @param summary The {@link Summary} instance containing the summary information (emailAddress and genes of
     *                interest). If the instance is a {@link SummaryWithDecoration}, the resulting string will contain
     *                gene state change decoration; otherwise, it will not.
     * @param inHtml Boolean indicating whether or not the output should be in html
     * @return A string containing the contact (named in the summary) e-mail content
     */
    public String getSummaryContent(Summary summary, boolean inHtml) {
        StringBuilder sb = new StringBuilder();

        sb
                .append(getSummaryPreface(inHtml))
                .append(getSummaryHtmlTableText(summary))
                .append(inHtml ? "<br />" : "\n");

        if ((summary instanceof SummaryWithDecoration) && (((SummaryWithDecoration) summary).isDecorated())){
            sb
                .append("* Gene assignment status has changed since the last e-mail sent to you.")
                .append(inHtml ? "<br /><br />" : "\n\n");
        }

        sb.append(getEpilogue(inHtml));


        return sb.toString();
    }


    /**
     *
     * @param inHtml boolean: if true, returned string is wrapped in HTML. If false, it is not.
     * @return An HTML string containing the static welcome text sent to newly registered contacts.
     */
    public String getWelcomeContent(boolean inHtml) {
        StringBuilder sb = new StringBuilder();

        sb
                .append("Dear colleague,")

                .append(inHtml ? "<br /><br />" : "\n\n")

                .append("Thank you for registering your interest in genes with the IMPC. As a benefit of having registered, ")
                .append("you will receive an e-mail notification whenever the status of the gene(s) for which you have registered ")
                .append("interest changes.\n")

                .append("\n")

                .append("You may register or unregister for any genes of your choice by either:\n")

                .append(inHtml ? "<ul>" : "")

                .append(inHtml ? "<li>" : "\t* ")
                .append("clicking the link in the ")
                .append(inHtml ? "<b>" : "")
                .append("Search")
                .append(inHtml ? "</b>" : "")
                .append(" ")
                .append("page's ")
                .append(inHtml ? "<i>" : "")
                .append("Register")
                .append(inHtml ? "</i>" : "")
                .append(" ")
                .append("column corresponding to the gene of interest, or ")
                .append(inHtml ? "</li>" : "\n")

                .append(inHtml ? "<li>" : "\t* ")
                .append("clicking on the button on the ")
                .append(inHtml ? "<b>" : "")
                .append("Gene")
                .append(inHtml ? "</b>" : "")
                .append(" ")
                .append("page just below the gene name")
                .append(inHtml ? "</li>" : "\n")

                .append(inHtml ? "</ul>" : "")

                .append("\n")

                .append("Clicking the ")
                .append(inHtml ? "<i>" : "")
                .append("My genes")
                .append(inHtml ? "</i>" : "")
                .append(" ")
                .append("link on the ")
                .append(inHtml ? "<b>" : "")
                .append("Search")
                .append(inHtml ? "</b>" : "")
                .append(" ")
                .append("page will take you to a page showing the genes for which you have already registered. On this page you may:")

                .append(inHtml ? "<ul>" : "")

                .append(inHtml ? "<li>" : "\t* ")
                .append("unregister by clicking the ")
                .append(inHtml ? "<i>" : "")
                .append("Unregister")
                .append(inHtml ? "</i>" : "")
                .append(" ")
                .append("button")
                .append(inHtml ? "</li>" : "\n")

                .append(inHtml ? "<li>" : "\t* ")
                .append("reset your password by clicking the ")
                .append(inHtml ? "<i>" : "")
                .append("Reset password")
                .append(inHtml ? "</i>" : "")
                .append(" ")
                .append("button")
                .append(inHtml ? "</li>" : "\n")

                .append(inHtml ? "<li>" : "\t* ")
                .append("delete your account by clicking the")
                .append(inHtml ? "<i>" : "")
                .append("Delete account")
                .append(inHtml ? "</i>" : "")
                .append(" ")
                .append("button ")
                .append(inHtml ? "<b><i>" : "")
                .append("Warning: deleting your account will delete all genes for which you have registered interest, as well ")
                .append("as any history. This action is permanent and cannot be undone, so please use with caution.")
                .append(inHtml ? "</i></b>" : "")

                .append(inHtml ? "</ul>\n" : "\n")
                .append("\n")

                .append(getEpilogue(inHtml))
        ;

        return sb.toString();
    }


    // PROTECTED METHODS


    protected static String getEpilogue(boolean inHtml) {

        StringBuilder body = new StringBuilder();

        body
                .append("You may review our e-mail list privacy policy at:")
                .append(inHtml ? "<br /><br />" : "\n\n")
                .append(SummaryHtmlTable.buildHtmlCell("div", PRIVACY_POLICY_LINK, PRIVACY_POLICY_LINK))
                .append(inHtml ? "<br />" : "\n")
                .append("For further information / enquiries please write to ")
                .append("<a href=\"mailto: " + MOUSEINFORMATICS_EMAIL + "\">" + MOUSEINFORMATICS_EMAIL + "</a>.")
                .append(inHtml ? "<br /><br />" : "\n\n")
                .append("Best Regards,\n")
                .append(inHtml ? "<br /><br />" : "\n\n")
                .append("The IMPC team");

        return body.toString();
    }

    /**
     * @param summary The {@link Summary} instance containing the summary information (emailAddress and genes of
     *                interest). If the instance is a {@link SummaryWithDecoration}, the resulting string will contain
     *                gene state change decoration; otherwise, it will not.
     * @return An HTML string containing this contact's summary information, in HTML table format
     */
    protected String getSummaryHtmlTableText(Summary summary) {
        return SummaryHtmlTable.buildTableContent(paBaseUrl, riBaseUrl, summary);
    }

    protected String getSummaryPreface(boolean inHtml) {
        StringBuilder sb = new StringBuilder();

        sb
                .append("Dear colleague,\n")

                .append(inHtml ? "<br /><br />" : "\n\n")

                .append("Below please find a summary of the IMPC genes for which you have registered interest.\n")

                .append(inHtml ? "<br /><br />" : "\n\n")

                .append("You have previously joined the IMPC ")
                .append(inHtml ? "<i>" : "'")
                .append("Register Interest")
                .append(inHtml ? "</i>" : "'")
                .append(" ")
                .append("list, which records your email address and genes for which you would like updates on mouse knockout, production, and phenotyping.\n")

                .append(inHtml ? "<br /><br />" : "\n\n")

                .append("You can unsubscribe from any gene for which you have registered interest by clicking on its corresponding ")
                .append(inHtml ? "<i>" : "'")
                .append("Unregister")
                .append(inHtml ? "</i>" : "'")
                .append(" ")
                .append("link in the")
                .append(inHtml ? "<b>" : "'")
                .append("Action")
                .append(inHtml ? "</b>" : "'")
                .append(" ")
                .append("column below.\n")

                .append(inHtml ? "<br /><br />" : "\n\n")

        ;

        return sb.toString();
    }
}