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

package org.mousephenotype.ri.web.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.ri.core.DateUtils;
import org.mousephenotype.ri.core.EmailUtils;
import org.mousephenotype.ri.core.SecurityUtils;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.Contact;
import org.mousephenotype.ri.core.entities.ResetCredentials;
import org.mousephenotype.ri.core.entities.Summary;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.mail.Message;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
public class SummaryController {

    private final int PASSWORD_CHANGE_TTL_MINUTES = 10;

    // Sleep intervals
    private final int INVALID_PASSWORD_SLEEP_SECONDS = 1;
    private final int SHORT_SLEEP_SECONDS            = 1;

    // Error messages
    public final static String ERR_ACCOUNT_LOCKED         = "Your account is locked.";
    public final static String ERR_EMAIL_ADDRESS_MISMATCH = "The e-mail addresses do not match.";
    public final static String ERR_INVALID_EMAIL_ADDRESS  = "The value provided is not a valid e-mail address. Please enter a valid e-mail address.";
    public final static String ERR_INVALID_TOKEN          = "Invalid token.";
    public final static String ERR_PASSWORD_MISMATCH      = "The passwords do not match.";
    public final static String ERR_CHANGE_PASSWORD_FAILED = "The password could not be changed.";
    public final static String ERR_SEND_MAIL_FAILED       = "The e-mail send failed.";
    public final static String ERR_INVALID_CREDENTIALS    = "Invalid username and password.";

    // Info messages
    public final static String INFO_PASSWORD_EXPIRED = "Your password is expired. Please use the <i>change password</i> link below to change your password.";
    public final static String INFO_CHANGE_PASSWORD  = "Send an e-mail to the address below to set/change the password.";


    // Title strings
    public final static String TITLE_ACCOUNT_LOCKED             = "Account locked";
    public final static String TITLE_INVALID_TOKEN              = "Invalid token";
    public final static String TITLE_INVALID_EMAIL_ADDRESS      = "Invalid e-mail address";
    public final static String TITLE_PASSWORD_EXPIRED           = "Password expired";
    public final static String TITLE_INVALID_CREDENTIALS        = "Invalid credentials";
    public final static String TITLE_PASSWORD_MISMATCH          = "Password mismatch";
    public final static String TITLE_EMAIL_ADDRESS_MISMATCH     = "e-mail address mismatch";
    public final static String TITLE_CHANGE_PASSWORD_FAILED     = "Change password failed";
    public final static String TITLE_CHANGE_PASSWORD_REQUEST    = "Change password";
    public final static String TITLE_CHANGE_PASSWORD_EMAIL_SENT = "Change password e-mail sent";
    public final static String TITLE_SEND_MAIL_FAILED           = "Mail server is down";

    private final org.slf4j.Logger logger        = LoggerFactory.getLogger(this.getClass());
    private       DateUtils        dateUtils     = new DateUtils();
    private       EmailUtils       emailUtils    = new EmailUtils();
    private       SecurityUtils    securityUtils = new SecurityUtils();

    // Properties
    private Map<String, String> config;
    private String              paContextRoot;
    private String              paHostname;
    private PasswordEncoder     passwordEncoder;
    private SqlUtils            sqlUtils;
    private String              smtpFrom;
    private String              smtpHost;
    private int                 smtpPort;
    private String              smtpReplyto;


    @Inject
    public SummaryController(
            String paContextRoot,
            String paHostname,
            PasswordEncoder passwordEncoder,
            SqlUtils sqlUtils,
            String smtpFrom,
            String smtpHost,
            int smtpPort,
            String smtpReplyto,
            Map<String, String> config
    ) {
        this.paContextRoot = paContextRoot;
        this.paHostname = paHostname;
        this.passwordEncoder = passwordEncoder;
        this.sqlUtils = sqlUtils;
        this.smtpFrom = smtpFrom;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpReplyto = smtpReplyto;
        this.config = config;
    }


    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String loginUrl(
            HttpServletRequest request,
            ModelMap model
    ) {
        String error = request.getQueryString();

        if (error != null) {
            sleep(INVALID_PASSWORD_SLEEP_SECONDS);
        }

        return "loginPage";
    }


    @RequestMapping(value = "failedLogin", method = RequestMethod.GET)
    public String failedLoginUrl(
            HttpServletRequest request
    ) {

        String error = request.getQueryString();
        if (error != null) {
            sleep(INVALID_PASSWORD_SLEEP_SECONDS);
        }

        return "redirect:/login?error";
    }


    @RequestMapping(value = "Access_Denied", method = RequestMethod.GET)
    public String accessDeniedUrl(
            ModelMap model
    ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Access is denied. Return to errorPage page.
        model.addAttribute("title", TITLE_INVALID_CREDENTIALS);
        model.addAttribute("error", ERR_INVALID_CREDENTIALS);

        List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        logger.info("/Access_Denied: No permission to access page for user {} with role {}", securityUtils.getPrincipal(), StringUtils.join(roles, ", "));

        sleep(SHORT_SLEEP_SECONDS);

        return "errorPage";
    }


    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logoutUrl(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.info("/logout: User {} logged out.", securityUtils.getPrincipal());
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:summary";
    }


    @RequestMapping(value = "summary", method = RequestMethod.GET)
    public String summaryUrl(ModelMap model, HttpServletRequest request) throws InterestException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Contact contact = sqlUtils.getContact(securityUtils.getPrincipal());

        if (contact == null) {
            model.addAttribute("title", TITLE_INVALID_CREDENTIALS);
            model.addAttribute("error", ERR_INVALID_CREDENTIALS);

            List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            logger.info("summaryUrl: Unable to get principal for user {} with role {}", securityUtils.getPrincipal(), StringUtils.join(roles, ", "));

            sleep(SHORT_SLEEP_SECONDS);

            return "errorPage";
        }

        // Redirect to error page if account is locked.
        if (contact.isAccountLocked()) {
            model.addAttribute("title", TITLE_ACCOUNT_LOCKED);
            model.addAttribute("error", ERR_ACCOUNT_LOCKED);

            return "errorPage";
        }

        // Redirect to chanbgePasswordRequestPage if password is expired.
        if (contact.isPasswordExpired()) {
            model.addAttribute("title", TITLE_PASSWORD_EXPIRED);
            model.addAttribute("status", INFO_PASSWORD_EXPIRED);
            model.addAttribute("showWhen", true);

            return "changePasswordRequestPage";
        }

        // Use the web service to get the data for the page.
        String cookie = getMySessionCookie(request);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        String wsUrl = paHostname + paContextRoot + "/api/summary";
        ResponseEntity<Summary> response = new RestTemplate().exchange(wsUrl, HttpMethod.GET, new HttpEntity<String>(headers), Summary.class);

        model.addAttribute("summary", response.getBody());

        return "summaryPage";
    }


    @RequestMapping(value = "changePasswordRequest", method = RequestMethod.GET)
    public String changePasswordRequestUrl(ModelMap model) {
        model.addAttribute("title", TITLE_CHANGE_PASSWORD_REQUEST);
        model.addAttribute("status", INFO_CHANGE_PASSWORD);

        return "changePasswordRequestPage";
    }


    @RequestMapping(value = "changePasswordEmail", method = RequestMethod.POST)
    public String changePasswordEmailUrl(
            ModelMap model,
            @RequestParam(value = "emailAddress", defaultValue = "") String emailAddress,
            @RequestParam(value = "repeatEmailAddress", defaultValue = "") String repeatEmailAddress)
    {
        // Validate e-mail addresses are identical.
        if ( ! emailAddress.equals(repeatEmailAddress)) {
            model.addAttribute("emailAddress", emailAddress);
            model.addAttribute("title", TITLE_EMAIL_ADDRESS_MISMATCH);
            model.addAttribute("error", ERR_EMAIL_ADDRESS_MISMATCH);

            return "changePasswordRequestPage";
        }

        // Validate that it looks like an e-mail address.
        if ( !  emailUtils.isValidEmailAddress(emailAddress)) {
            model.addAttribute("title", TITLE_INVALID_EMAIL_ADDRESS);
            model.addAttribute("error", ERR_INVALID_EMAIL_ADDRESS);

            return "changePasswordRequestPage";
        }

        // Generate and assemble email with password change
        String token     = buildToken(emailAddress);
        String tokenLink = paHostname + paContextRoot + "/changePasswordResponse?token=" + token;
        System.out.println("tokenLink = " + tokenLink);
        String  body    = generateChangePasswordEmail(tokenLink);
        String  subject = "Change IMPC Register Interest password link";
        Message message = emailUtils.assembleEmail(smtpHost, smtpPort, smtpFrom, smtpReplyto, subject, body, emailAddress, true);

        // Insert request to reset_credentials table
        ResetCredentials resetCredentials = new ResetCredentials(emailAddress, token, new Date());
        sqlUtils.updateResetCredentials(resetCredentials);

        // Send e-mail
        try {

            emailUtils.sendEmail(message);

        } catch (InterestException e) {

            model.addAttribute("title", TITLE_SEND_MAIL_FAILED);
            model.addAttribute("error", ERR_SEND_MAIL_FAILED);
            logger.error("Error trying to send password change e-mail to {}: {}", emailAddress, e.getLocalizedMessage());
            return "errorPage";
        }

        String status = "An e-mail containing a change password link has been sent to <i>" + emailAddress + "</i>.\n" +
                        "Any previous links are no longer valid. This link is valid for " + PASSWORD_CHANGE_TTL_MINUTES + " minutes.";
        model.addAttribute("emailAddress", emailAddress);
        model.addAttribute("title", TITLE_CHANGE_PASSWORD_EMAIL_SENT);
        model.addAttribute("status", status);
        model.addAttribute("showWhen", true);

        logger.info("Sent Change Password email to {}", emailAddress);

        return "statusPage";
    }


    @RequestMapping(value = "changePasswordResponse", method = RequestMethod.GET)
    public String changePasswordResponseGetUrl(ModelMap model, HttpServletRequest request) {

        // Parse out query string for token value.
        String token = getTokenFromQueryString(request.getQueryString());

        // Look up email address from reset_credentials table
        ResetCredentials resetCredentials = sqlUtils.getResetCredentials(token);

        // If not found, return to errorPage page.
        if (resetCredentials == null) {
            model.addAttribute("title", TITLE_INVALID_TOKEN);
            model.addAttribute("error", ERR_INVALID_TOKEN);

            logger.info("Token {} not found", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "errorPage";
        }

        // If token has expired, return to errorPage page.
        if (dateUtils.isExpired(resetCredentials.getCreatedAt(), PASSWORD_CHANGE_TTL_MINUTES)) {
            model.addAttribute("title", TITLE_INVALID_TOKEN);
            model.addAttribute("error", ERR_INVALID_TOKEN);

            logger.info("Token {} has expired", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "errorPage";
        }

        model.addAttribute("token", token);

        return "changePasswordResponsePage";
    }


    @RequestMapping(value = "changePasswordResponse", method = RequestMethod.POST)
    public String changePasswordResponsePostUrl(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                       @RequestParam("token") String token,
                                       @RequestParam("newPassword") String newPassword,
                                       @RequestParam("repeatPassword") String repeatPassword)
    {
        model.addAttribute("token", token);

        // Validate the new password. Return to changePasswordResponsePage if validation fails.
        String error = validateNewPassword(newPassword, repeatPassword);
        if ( ! error.isEmpty()) {
            model.addAttribute("title", TITLE_PASSWORD_MISMATCH);
            model.addAttribute("error", ERR_PASSWORD_MISMATCH);

            logger.info("Token {} not found", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "changePasswordResponsePage";
        }

        // Look up email address from reset_credentials table
        ResetCredentials resetCredentials = sqlUtils.getResetCredentials(token);

        // If not found, return to errorPage page.
        if (resetCredentials == null) {
            model.addAttribute("title", TITLE_INVALID_TOKEN);
            model.addAttribute("error", ERR_INVALID_TOKEN);

            logger.info("Token {} not found", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "errorPage";
        }

        String emailAddress = resetCredentials.getAddress();

        // Encrypt the password, update it in the contact table, and consume (remove) reset_credential record.
        try {

            updatePassword(emailAddress, passwordEncoder.encode(newPassword));
            sqlUtils.deleteResetCredentialsByEmailAddress(emailAddress);

        } catch (InterestException e) {

            model.addAttribute("title", TITLE_CHANGE_PASSWORD_FAILED);
            model.addAttribute("error", ERR_CHANGE_PASSWORD_FAILED);
            logger.error("Error trying to change password for {}: {}", emailAddress, e.getLocalizedMessage());
            return "errorPage";
        }

        logger.info("Password successfully changed for {}", emailAddress);

        // Get the user's roles and mark the user as authenticated.
        Contact contact = sqlUtils.getContact(emailAddress);
        Authentication  authentication  = new UsernamePasswordAuthenticationToken(emailAddress, null, contact.getRoles());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        model.addAttribute("statusTitle", "Password is change");
        model.addAttribute("status", "Your password has been changed.");
        model.addAttribute("emailAddress", emailAddress);

        return "statusPage";
    }


    // PRIVATE METHODS


    /**
     * Build and return a unique hash token for this email address
     *
     * @param emailAddress
     * @return a unique hash token for this email address
     */
    private String buildToken(String emailAddress) {

        String token;

        SecureRandom random       = new SecureRandom();
        String       randomDouble = Double.toString(random.nextDouble());

        token = DigestUtils.sha256Hex(randomDouble + emailAddress);

        return token;
    }

    private final String EMAIL_STYLE = new StringBuilder()
            .append("<style>")
            .append(".button {")
            .append("background-color: #286090;")
            .append("border-color: #204d74;")
            .append("border-radius: 5px;")
            .append("text-align: center;")
            .append("padding: 10px;")
            .append("}")
            .append(".button a {")
            .append("color: #ffffff;")
            .append("display: block;")
            .append("font-size: 14px;")
            .append("text-decoration: none;")
            .append("}")
            .append("</style>").toString();

    private String generateChangePasswordEmail(String tokenLink) {

        StringBuilder body = new StringBuilder()
                .append("<html>")
                .append(EMAIL_STYLE)
                .append("<table>")
                .append("<tr>")
                .append("<td>")
                .append("Dear colleague,")
                .append("<br />")
                .append("<br />")
                .append("This e-mail was sent in response to a request to change your IMPC Register Interest password. ")
                .append("If you made no such request, please ignore this e-mail; otherwise, please click on the link ")
                .append("below to change your IMPC Register Interest password.")
                .append("<br />")
                .append("<br />")
                .append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td class=\"button\">")
                .append("<a href=\"" + tokenLink + "\">Reset password</a>")
                .append("</td>")
                .append("</tr>")
                .append("</table>")
                .append("</html>");

        return body.toString();
    }

    /**
     * @return my JSESSIONID cookie string
     *
     */
    private String getMySessionCookie(HttpServletRequest request) {

        String session = "";

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    session = cookie.getName() + "=" + cookie.getValue();
                    break;
                }
            }
        }

        return session;
    }

    private String getTokenFromQueryString(String queryString) {

        if ((queryString == null) || (queryString.isEmpty())) {
            return "";
        }

        String[] pieces = StringUtils.split(queryString, "=");
        if ((pieces.length != 2) && (!pieces[0].equals("token"))) {
            return "";
        }

        return pieces[1];
    }

    private void sleep(int numSeconds) {
        try {
            Thread.sleep(numSeconds * 1000);

        } catch (InterruptedException e) {
        }
    }

    private void updatePassword(String emailAddress, String encryptedPassword) throws InterestException {

        sqlUtils.updatePassword(emailAddress, encryptedPassword);
    }

    private String validateNewPassword(String newPassword, String repeatPassword) {

        if (newPassword.isEmpty()) {
            return "Please specify a new password";
        }

        if ( ! newPassword.equals(repeatPassword)) {
            return "Passwords do not match";
        }

        return "";
    }
}