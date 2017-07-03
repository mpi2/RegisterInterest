package org.mousephenotype.ri.send;

import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.GeneSent;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import java.util.*;

/**
 * Created by mrelac on 05/06/2017.
 *
 * This class is intended to be a command-line callable java main program that sends previously generated e-mails to
 * contacts registered for insterest in specific genes, diseases, or phenotypes whose status indicates the state has changed.
 */
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private DataSource riDataSource;

    @Autowired
    private SqlUtils sqlUtils;


    @Value("${mail.smtp.auth}")
    private Boolean smtpAuth;

    @Value("${mail.smtp.starttls.enable}")
    private Boolean smtpStarttlsEnable;

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private Integer smtpPort;

    @Value("${mail.smtp.username}")
    private String smtpUsername;

    @Value("${mail.smtp.password}")
    private String smtpPassword;

    @Value("${mail.smtp.from}")
    private String smtpFrom;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<Integer, String> emailAddressesByGeneContactPk;
    private List<GeneSent> genesScheduledForSending;



    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {

        emailAddressesByGeneContactPk = sqlUtils.getEmailAddressesByGeneContactPk();
        genesScheduledForSending = sqlUtils.getGenesScheduledForSending();
        int built = 0;
        int sent = 0;
        Message message;

        for (GeneSent geneSent : genesScheduledForSending) {
            String email = emailAddressesByGeneContactPk.get(geneSent.getGeneContactPk());
            message = buildEmail(geneSent, email);
            built++;

//            sendEmail(message);
//            sent++;
        }

        System.out.println("Built " + built + " emails.");
        System.out.println("Sent " + sent + " emails.");
    }


    // PRIVATE METHODS


    private Message buildEmail(GeneSent gene, String email) {

        Properties smtpProperties = new Properties();
        smtpProperties.put("mail.smtp.auth", smtpAuth);
        smtpProperties.put("mail.smtp.starttls.enable", smtpStarttlsEnable);
        smtpProperties.put("mail.smtp.host", smtpHost);
        smtpProperties.put("mail.smtp.port", smtpPort);

        Session session = Session.getInstance(smtpProperties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(smtpUsername, smtpPassword);
                    }
                });

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

        try {
            recipient = message.getRecipients(Message.RecipientType.TO)[0].toString();

            Transport.send(message);
            geneSent.setSentAt(new Date());
            sqlUtils.updateOrInsertGeneSent(geneSent);

        } catch (MessagingException e) {

            throw new InterestException("SEND of message to " + recipient + " failed: " + e.getLocalizedMessage());
        }
    }
}