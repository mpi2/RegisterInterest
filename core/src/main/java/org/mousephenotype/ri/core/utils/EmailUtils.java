/*******************************************************************************
 *  Copyright © 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.ri.core.utils;

import org.mousephenotype.ri.core.exceptions.InterestException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * This class encapsulates the code and data necessary to manage the generation and sending of emails.
 *
 * NOTE: Please do not add any methods here that require being wired in to Spring. Keep this file spring-free, as it
 *       is used in places that are not spring-dependent.
 *
 * Created by mrelac on 02/07/2015.
 */
public class EmailUtils {

    /**
     * Assembles an e-mail in preparation for sending.
     * @param smtpHost
     * @param smtpPort
     * @param smtpFrom
     * @param smtpReplyto
     * @param subject
     * @param body
     * @param emailAddress
     * @param inHtml
     * @return {@link Message} the assembled email message, ready for sending
     */
    public Message assembleEmail(

            String smtpHost, Integer smtpPort, String smtpFrom, String smtpReplyto,
            String subject, String body, String emailAddress, boolean inHtml) {

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
                                  InternetAddress.parse(emailAddress));
            message.setSubject(subject);
            if (inHtml) {
                message.setContent(body, "text/html; charset=utf-8");
            } else {
                message.setText(body);
            }

        } catch (MessagingException e) {

            throw new RuntimeException(e);
        }

        return message;
    }

    /**
     * Sends the e-mail {@link Message} to the recipient specified as the first recipient in the message
     * @param message
     * @throws InterestException
     */
    public void sendEmail(Message message) throws InterestException {

        String recipient = null;

        try {
            recipient = message.getRecipients(Message.RecipientType.TO)[0].toString();

            Transport.send(message);

        } catch (MessagingException e) {

            throw new InterestException("SEND of message to " + recipient + " failed: " + e.getLocalizedMessage());
        }
    }

    public boolean isValidEmailAddress(String proposedEmailAddress) {

        boolean result = false;

        try {

            InternetAddress emailAddress = new InternetAddress(proposedEmailAddress);
            emailAddress.validate();
            result = true;

        } catch (AddressException ex) {
            // Nothing to do
        }

        return result;
    }
}