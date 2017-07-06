package org.mousephenotype.ri.send;

import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.GeneContact;
import org.mousephenotype.ri.core.entities.GeneSent;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Inject
    public ApplicationSend(SqlUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }


    private Map<Integer, String> emailAddressesByGeneContactPk;
    private List<GeneSent> genesScheduledForSending;
    private Map<Integer, GeneContact> geneContacts = new HashMap<>();



    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ApplicationSend.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {

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
            message = buildEmail(geneSent, email);
            built++;

            sendEmail(geneSent, message);
            sent++;
        }

        System.out.println("Built " + built + " emails.");
        System.out.println("Sent " + sent + " emails.");
    }


    // PRIVATE METHODS


    private Message buildEmail(GeneSent gene, String email) {

        Properties smtpProperties = new Properties();

        smtpProperties.put("mail.smtp.host", smtpHost);
        smtpProperties.put("mail.smtp.port", smtpPort);

        Session session = Session.getInstance(smtpProperties);
        Message message = new MimeMessage(session);

        try {

            message.setFrom(new InternetAddress(smtpFrom));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject(gene.getSubject());
            message.setText(gene.getBody());

        } catch (MessagingException e) {

            throw new RuntimeException(e);
        }

        return message;
    }

    private void sendEmail(GeneSent geneSent, Message message) throws InterestException {

        String recipient = null;
        GeneContact gc = geneContacts.get(geneSent.getGeneContactPk());

        try {
            recipient = message.getRecipients(Message.RecipientType.TO)[0].toString();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String invoker = (auth == null ? "Unknown" : auth.getName());

            Transport.send(message);
            geneSent.setSentAt(new Date());
            sqlUtils.updateOrInsertGeneSent(geneSent);
            String logMessage = "email scheduled for transport " + geneSent.getSentAt() + " for genePk " + gc.getGenePk() + ", contactPk " + gc.getContactPk() + ": OK";
            sqlUtils.logSendAction(invoker, gc.getGenePk(), gc.getContactPk(), logMessage);

        } catch (MessagingException e) {

            throw new InterestException("SEND of message to " + recipient + " failed: " + e.getLocalizedMessage());
        }
    }
}