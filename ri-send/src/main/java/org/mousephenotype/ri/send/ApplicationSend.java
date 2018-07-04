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

package org.mousephenotype.ri.send;

import org.mousephenotype.ri.core.EmailUtils;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.Contact;
import org.mousephenotype.ri.core.entities.ContactGene;
import org.mousephenotype.ri.core.entities.GeneSent;
import org.mousephenotype.ri.core.entities.GeneSentSummary;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mrelac on 05/06/2017.
 *
 * This class is intended to be a command-line callable java main program that sends previously generated e-mails to
 * contacts registered for insterest in specific genes, diseases, or phenotypes whose status indicates the state has changed.
 */
@SpringBootApplication
public class ApplicationSend implements CommandLineRunner {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private SqlUtils sqlUtils;

    @NotNull
    @Value("${mail.smtp.host}")
    private String smtpHost;

    @NotNull
    @Value("${mail.smtp.port}")
    private Integer smtpPort;

    @NotNull
    @Value("${mail.smtp.from}")
    private String smtpFrom;

    @NotNull
    @Value("${mail.smtp.replyto}")
    private String smtpReplyto;

    private EmailUtils emailUtils = new EmailUtils();


    @Inject
    public ApplicationSend(SqlUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }


    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ApplicationSend.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {


        // If there are pending gene_sent_summary emails:
        //    invoke doGeneSentSummary() to send all gene_sent_summary emails with null sent_at dates (they should all be null)
        // else
        //    invoke doGeneSent() to send all gene_sent emails with null sent_at dates

        int pendingGeneSentSummaryCount = sqlUtils.getGeneSentSummaryPendingEmailCount();
        if (pendingGeneSentSummaryCount > 0) {

            doGeneSentSummary();

        } else {

            // FIXME Don't execute this when mailing summary.
//            doGeneSent();

        }
    }


    // PRIVATE METHODS


    private void doGeneSent() throws InterestException {

        Map<Integer, String> emailAddressesByContactGenePk;
        List<GeneSent> genesScheduledForSending;
        Map<Integer, ContactGene> contactGenes = new HashMap<>();


        emailAddressesByContactGenePk = sqlUtils.getEmailAddressesByContactGenePk();
        genesScheduledForSending = sqlUtils.getGenesScheduledForSending();
        List<ContactGene> contactGeneList = sqlUtils.getContactGenes();
        for (ContactGene gc : contactGeneList) {
            contactGenes.put(gc.getPk(), gc);
        }
        int built = 0;
        int sent = 0;
        Message message;

        for (GeneSent geneSent : genesScheduledForSending) {
            String email = emailAddressesByContactGenePk.get(geneSent.getContactGenePk());
            ContactGene contactGene = contactGenes.get(geneSent.getContactGenePk());
            boolean isHtml = false;
            message = emailUtils.assembleEmail(smtpHost, smtpPort, smtpFrom, smtpReplyto, geneSent.getSubject(), geneSent.getBody(), email, isHtml);
            built++;

            sendEmail(contactGene, geneSent, message);
            sent++;

            // Pause for 36 seconds so we don't exceed 100 e-mails per hour.
            try {
                Thread.sleep(36000);
            } catch (InterruptedException e) {
                throw new InterestException("Attempt to Thread.sleep failed: " + e.getLocalizedMessage());
            }
        }

        System.out.println("Built " + built + " emails.");
        System.out.println("Sent " + sent + " emails.");
    }

    private void doGeneSentSummary() throws InterestException {
        int built = 0;
        int sent = 0;
        Message message;
        Map<Integer, GeneSentSummary> summaryMap = sqlUtils.getGeneSentSummary();
        Map<Integer, Contact> contactMap = sqlUtils.getContactsIndexedByContactPk();

        for (Map.Entry<Integer, GeneSentSummary> entry : summaryMap.entrySet()) {
            int contactPk = entry.getKey();
            GeneSentSummary summary = entry.getValue();

            // Skip any rows whose summary.getSentAt() is not null. They have already been sent.
            if (summary.getSentAt() != null) {
                continue;
            }

            String email = contactMap.get(contactPk).getEmailAddress();
            boolean isHtml = true;
            message = emailUtils.assembleEmail(smtpHost, smtpPort, smtpFrom, smtpReplyto, summary.getSubject(), summary.getBody(), email, isHtml);
            built++;

            Date now = new Date();
            summary.setSentAt(now);

            try {

                sendSummaryEmail(summary, message);
                logger.info(email);

            } catch (Exception e) {

                logger.warn("Skipping {}", email);
                continue;

            }
            sent++;

            // Update the contact's gene_sent.sent_at (for all registered genes) with the same sent_at as used for the summary.
            GeneSent geneSent = new GeneSent();

            sqlUtils.updateGeneSentDates(email, now);

            // Pause for 36 seconds so we don't exceed 100 e-mails per hour.
            try {
                Thread.sleep(36000);
            } catch (InterruptedException e) {
                throw new InterestException("Attempt to Thread.sleep failed: " + e.getLocalizedMessage());
            }
        }

        System.out.println("Built " + built + " emails.");
        System.out.println("Sent " + sent + " emails.");
    }

    // FIXME contactGene is never used
    private void sendEmail(ContactGene contactGene, GeneSent geneSent, Message message) throws InterestException {

        String recipient = null;

        try {
            recipient = message.getRecipients(Message.RecipientType.TO)[0].toString();

            Authentication auth    = SecurityContextHolder.getContext().getAuthentication();
            String         invoker = (auth == null ? "Unknown" : auth.getName());

            Transport.send(message);
            geneSent.setSentAt(new Date());
            sqlUtils.insertGeneSent(geneSent);

        } catch (MessagingException e) {

            throw new InterestException("SEND of message to " + recipient + " failed: " + e.getLocalizedMessage());
        }
    }

    private void sendSummaryEmail(GeneSentSummary summary, Message message) throws InterestException {

        String recipient = null;

        try {
            recipient = message.getRecipients(Message.RecipientType.TO)[0].toString();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String invoker = (auth == null ? "Unknown" : auth.getName());

            Transport.send(message);
            sqlUtils.updateGeneSentSummary(summary);

        } catch (MessagingException e) {

            throw new InterestException("SEND of message to " + recipient + " failed. Skipping... Reason: " + e.getLocalizedMessage());
        }
    }
}