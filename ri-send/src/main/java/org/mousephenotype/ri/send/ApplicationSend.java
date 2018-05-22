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

import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.Contact;
import org.mousephenotype.ri.core.entities.GeneContact;
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
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.NotNull;
import java.util.*;

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
        //    Mark all gene_sent sent_at dates with the current date to indicate all contact e-mails are up-to-date
        // else
        //    invoke doGeneSent() to send all gene_sent emails with null sent_at dates


        int pendingGeneSentSummaryCount = sqlUtils.getGeneSentSummaryPendingEmailCount();
        if (pendingGeneSentSummaryCount > 0) {
            doGeneSentSummary();
            // Mark all gene_sent.sent_at dates with the current date to indicate all contact e-mails are up-to-date.
            sqlUtils.updateAllGeneSentDates(new Date());
        } else {
            doGeneSent();
        }
    }


    // PRIVATE METHODS


    private void doGeneSent() throws InterestException {

        Map<Integer, String> emailAddressesByGeneContactPk;
        List<GeneSent> genesScheduledForSending;
        Map<Integer, GeneContact> geneContacts = new HashMap<>();


        emailAddressesByGeneContactPk = sqlUtils.getEmailAddressesByGeneContactPk();
        genesScheduledForSending = sqlUtils.getGenesScheduledForSending();
        List<GeneContact> geneContactList = sqlUtils.getGeneContacts();
        for (GeneContact gc : geneContactList) {
            geneContacts.put(gc.getPk(), gc);
        }
        int built = 0;
        int sent = 0;
        Message message;

        for (GeneSent geneSent : genesScheduledForSending) {
            String email = emailAddressesByGeneContactPk.get(geneSent.getGeneContactPk());
            GeneContact geneContact = geneContacts.get(geneSent.getGeneContactPk());
            boolean isHtml = false;
            message = buildEmail(geneSent.getSubject(), geneSent.getBody(), email, isHtml);
            built++;

            sendEmail(geneContact, geneSent, message);
            sent++;
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
            String email = contactMap.get(contactPk).getAddress();
            boolean isHtml = true;
            message = buildEmail(summary.getSubject(), summary.getBody(), email, isHtml);
            built++;

            sendSummaryEmail(summary, message);
            sent++;
        }

        System.out.println("Built " + built + " emails.");
        System.out.println("Sent " + sent + " emails.");
    }


    private Message buildEmail(String subject, String body, String email, boolean isHtml) {

        Properties smtpProperties = new Properties();

        smtpProperties.put("mail.smtp.host", smtpHost);
        smtpProperties.put("mail.smtp.port", smtpPort);

        Session session = Session.getInstance(smtpProperties);
        Message message = new MimeMessage(session);

        try {

            message.setFrom(new InternetAddress(smtpFrom));
            InternetAddress[] replyToArray = new InternetAddress[] { new InternetAddress(smtpReplyto) };
            message.setReplyTo(replyToArray);
            message.setRecipients(Message.RecipientType.TO,
                                  InternetAddress.parse(email));
            message.setSubject(subject);
            if (isHtml) {
                message.setContent(body, "text/html; charset=utf-8");
            } else {
                message.setText(body);
            }

        } catch (MessagingException e) {

            throw new RuntimeException(e);
        }

        return message;
    }

    private void sendEmail(GeneContact geneContact, GeneSent geneSent, Message message) throws InterestException {

        String recipient = null;

        try {
            recipient = message.getRecipients(Message.RecipientType.TO)[0].toString();

            Authentication auth    = SecurityContextHolder.getContext().getAuthentication();
            String         invoker = (auth == null ? "Unknown" : auth.getName());

            Transport.send(message);
            geneSent.setSentAt(new Date());
            sqlUtils.insertGeneSent(geneSent);
            String logMessage = "email scheduled for transport " + geneSent.getSentAt() + " for genePk " + geneContact.getGenePk() + ", contactPk " + geneContact.getContactPk() + ": OK";
            sqlUtils.logSendAction(invoker, geneContact.getGenePk(), geneContact.getContactPk(), logMessage);

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
            summary.setSentAt(new Date());
            sqlUtils.updateGeneSentSummary(summary);
            String logMessage = "summary email scheduled for transport " + summary.getSentAt() + " for contactPk " + summary.getContactPk() + ": OK";
            sqlUtils.logSendAction(invoker, null, summary.getContactPk(), logMessage);

        } catch (MessagingException e) {

            throw new InterestException("SEND of message to " + recipient + " failed: " + e.getLocalizedMessage());
        }
    }
}