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
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
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

    private final int PASSWORD_RESET_TTL_MINUTES = 10;

    // Sleep intervals
    private final int INVALID_PASSWORD_SLEEP_SECONDS = 3;
    private final int SHORT_SLEEP_SECONDS            = 1;

    // Error messages
    public final static String ERR_ACCOUNT_LOCKED    = "Your account is locked.";
    public final static String ERR_INVALID_TOKEN     = "Invalid token.";
    public final static String ERR_NO_PERMISSION     = "You do not have permission to access this page.";
    public final static String ERR_PASSWORD_BAD      = "Invalid username and password";
    public final static String ERR_PASSWORD_MISMATCH = "Passwords do not match.";

    // Info messages
    public final static String INFO_PASSWORD_EXPIRED = "Your password is expired. Please use the <i>Reset password</i> link to reset your password.";

    // Title strings
    public final static String TITLE_ACCOUNT_LOCKED    = "Account locked";
    public final static String TITLE_INVALID_TOKEN     = "Invalid token";
    public final static String TITLE_PASSWORD_EXPIRED  = "Password expired";
    public final static String TITLE_INVALID_CRED      = "Invalid credentials";
    public final static String TITLE_PASSWORD_MISMATCH = "Password mismatch";

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

    private String failedUsername;              // Used when authentication fails to get the login box username value.


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


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginUrl(
            HttpServletRequest request,
            ModelMap model) {

        String error = request.getQueryString();

//        if (failedUsername != null) {
//
//            // Check for account exists and is either locked or password-expired.
//            Contact contact = sqlUtils.getContact(failedUsername);
//
//
//
//
//
//            if (contact.isAccountLocked()) {
//                model.addAttribute("title", TITLE_ACCOUNT_LOCKED);
//                model.addAttribute("error", ERR_ACCOUNT_LOCKED);
//
//                logger.info("Attempted login by {} to locked account", failedUsername);
//
//                sleep(SHORT_SLEEP_SECONDS);
//
//                return "errorPage";
//            }
//
//            if (contact.isPasswordExpired()) {
//                model.addAttribute("emailAddress", failedUsername);
//                model.addAttribute("status", "Your password is expired. Please send an e-mail to the address below to reset the password.");
//                logger.info("Attempted login by {} to password-expired account", failedUsername);
//
//                String status = "Your password is expired. Please send an e-mail to the address below to reset the password.";
//                return "resetPasswordRequestPage";
//            }

//        if (UrlUtils.getReferer(request).equals("loginPage")) {
//            logger.info("/summary: User {} logged in.", emailAddress);
//        }





//            if (contact != null) {
//                if (contact.isAccountLocked()) {
//                    model.addAttribute("title", TITLE_ACCOUNT_LOCKED);
//                    model.addAttribute("error", ERR_ACCOUNT_LOCKED);
//                    return "errorPage";
//                }
//
//
//
//
//
//
//
//
//                if (contact.isPasswordExpired()) {
//                    model.addAttribute("title", TITLE_PASSWORD_EXPIRED);
//                    model.addAttribute("status", INFO_PASSWORD_EXPIRED);
//                    return "statusPage";
//                }
//            }
//        }


        // Sleep only if an error occurred.
        if (error != null) {
            model.addAttribute("title", TITLE_INVALID_CRED);
            model.addAttribute("error", ERR_NO_PERMISSION);
            sleep(INVALID_PASSWORD_SLEEP_SECONDS);
        }

        return "loginPage";
    }






    @RequestMapping(value = "/handleLogin", method = RequestMethod.GET)
    public String loginFailUrl(
            HttpServletRequest request,
            ModelMap model) {

        String error = request.getQueryString();

        if (failedUsername != null) {

            // Check for account exists and is either locked or password-expired.
            Contact contact = sqlUtils.getContact(failedUsername);





            if (contact.isAccountLocked()) {
                model.addAttribute("title", TITLE_ACCOUNT_LOCKED);
                model.addAttribute("error", ERR_ACCOUNT_LOCKED);

                logger.info("Attempted login by {} to locked account", failedUsername);

                sleep(SHORT_SLEEP_SECONDS);

                return "errorPage";
            }

            if (contact.isPasswordExpired()) {
                model.addAttribute("emailAddress", failedUsername);
                model.addAttribute("status", "Your password is expired. Please send an e-mail to the address below to reset the password.");
                logger.info("Attempted login by {} to password-expired account", failedUsername);

                String status = "Your password is expired. Please send an e-mail to the address below to reset the password.";
                return "resetPasswordRequestPage";
            }
        }


        // Sleep only if an error occurred.
        if (error != null) {
            model.addAttribute("title", TITLE_INVALID_CRED);
            model.addAttribute("error", ERR_NO_PERMISSION);
            sleep(INVALID_PASSWORD_SLEEP_SECONDS);
        }

        return "loginPage";
    }






    @RequestMapping(value = "/Access_Denied", method = RequestMethod.GET)
    public String accessDeniedPage(ModelMap model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Access is denied. Return to errorPage page.
        model.addAttribute("title", TITLE_INVALID_CRED);
        model.addAttribute("error", ERR_NO_PERMISSION);

        List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        logger.info("/Access_Denied: No permission to access page for user {} with role {}", securityUtils.getPrincipal(), StringUtils.join(roles, ", "));

        sleep(SHORT_SLEEP_SECONDS);

        return "errorPage";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
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

        String emailAddress = securityUtils.getPrincipal();

//        Contact contact = sqlUtils.getContact(emailAddress);
//        if (contact.isAccountLocked()) {
//            model.addAttribute("title", TITLE_ACCOUNT_LOCKED);
//            model.addAttribute("error", ERR_ACCOUNT_LOCKED);
//
//            logger.info("Attempted login by {} to locked account", emailAddress);
//
//            sleep(SHORT_SLEEP_SECONDS);
//
//            return "errorPage";
//        }

//        if (contact.isPasswordExpired()) {
////            model.addAttribute("emailAddress", emailAddress);
////            model.addAttribute("status", "Your password is expired. Send an e-mail to the address below to reset the password.");
//            String status = "Your password is expired. Send an e-mail to the address below to reset the password";
//            logger.info("Attempted login by {} to password-expired account", emailAddress);
//
//            return "redirect:/resetPasswordRequestPage&emailAddress=" + emailAddress + "&status=" + status;
//        }

//        if (UrlUtils.getReferer(request).equals("loginPage")) {
//            logger.info("/summary: User {} logged in.", emailAddress);
//        }

        // Use the web service to get the data for the page.
        String cookie = getMySessionCookie(request);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        ResponseEntity<Summary> response = new RestTemplate().exchange("http://localhost:8081/data/interest/api/summary", HttpMethod.GET, new HttpEntity<String>(headers), Summary.class);

        model.addAttribute("summary", response.getBody());

        return "summaryPage";
    }


    @RequestMapping(value = "newAccountRequest", method = RequestMethod.GET)
    public String newAccountRequestUrl() {

        return "newAccountRequestPage";
    }


    @RequestMapping(value = "newAccountEmail", method = RequestMethod.POST)
    public String newAccountEmailUrl(
            ModelMap model,
            @RequestParam("emailAddress") String emailAddress) throws InterestException {
        if (!sqlUtils.isValidEmailAddress(emailAddress)) {
            logger.info("Invalid email address '{}' created by anonymous user was rejected.", emailAddress);

            sleep(SHORT_SLEEP_SECONDS);

            return "redirect:/newAccountRequest?error=true";
        }

        model.addAttribute("PASSWORD_RESET_TTL_MINUTES", PASSWORD_RESET_TTL_MINUTES);
        model.addAttribute("emailAddress", emailAddress);

        Contact contact = sqlUtils.getContact(emailAddress);

        // Ignore the request if the account already exists.
        if (contact == null) {
            // Generate and assemble email with password reset
            String token     = buildToken(emailAddress);
            String tokenLink = paHostname + paContextRoot + "/newAccount?token=" + token;
            System.out.println("tokenLink = " + tokenLink);
            String  body    = generateNewAccountEmail(tokenLink);
            String  subject = "Request to register for new IMPC Register Interest account";
            Message message = emailUtils.assembleEmail(smtpHost, smtpPort, smtpFrom, smtpReplyto, subject, body, emailAddress, true);

            // Insert add request to reset_credentials table
            ResetCredentials resetCredentials = new ResetCredentials(emailAddress, token, new Date());
            sqlUtils.updateResetCredentials(resetCredentials);

            // Send e-mail
            emailUtils.sendEmail(message);
            logger.info("Sent New Account email to {}", emailAddress);
        } else {
            logger.info("Ignored New Account email request to {}. emailAddress already exists.", emailAddress);
        }

        return "newAccountSendPage";
    }


    @RequestMapping(value = "newAccount", method = RequestMethod.GET)
    public String newAccountUrlGet(ModelMap model, HttpServletRequest request) {

        // Parse out query string for token value.
        String token = getTokenFromQueryString(request.getQueryString());

        // Look up email address from reset_credentials table
        ResetCredentials resetCredentials = sqlUtils.getResetCredentials(token);

        // If not found, return to errorPage page.
        if (resetCredentials == null) {
            model.addAttribute("title", TITLE_INVALID_TOKEN);
            model.addAttribute("error", ERR_INVALID_TOKEN);

            logger.info("Token {} not found.", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "errorPage";
        }

        // If token has expired, return to errorPage page.
        if (dateUtils.isExpired(resetCredentials.getCreatedAt(), PASSWORD_RESET_TTL_MINUTES)) {
            model.addAttribute("title", TITLE_INVALID_TOKEN);
            model.addAttribute("error", ERR_INVALID_TOKEN);

            logger.info("Token {} has expired.", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "errorPage";
        }

        // Add token to model.
        model.addAttribute("token", token);

        return "newAccountPage";
    }


    @RequestMapping(value = "newAccount", method = RequestMethod.POST)
    public String newAccountUrlPost(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam("token") String token,
                                    @RequestParam("newPassword") String newPassword,
                                    @RequestParam("repeatPassword") String repeatPassword) throws InterestException {
        model.addAttribute("token", token);

        // Validate the new password. Return to resetPassword page if validation fails.
        String error = validateNewPassword(newPassword, repeatPassword);
        if (!error.isEmpty()) {
            model.addAttribute("error", TITLE_PASSWORD_MISMATCH);
            model.addAttribute("error", ERR_PASSWORD_MISMATCH);

            sleep(SHORT_SLEEP_SECONDS);

            return "newAccountPage";
        }

        // Look up email address from reset_credentials table
        ResetCredentials resetCredentials = sqlUtils.getResetCredentials(token);

        // If not found, return to errorPage page.
        if (resetCredentials == null) {
            model.addAttribute("title", TITLE_INVALID_TOKEN);
            model.addAttribute("error", ERR_INVALID_TOKEN);

            logger.info("No credentials found for {}", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "errorPage";
        }

        String emailAddress = resetCredentials.getAddress();

        try {
            sqlUtils.insertContact(emailAddress, newPassword);
        } catch (InterestException e) {

            model.addAttribute("title", TITLE_INVALID_TOKEN);
            model.addAttribute("error", ERR_INVALID_TOKEN);

            logger.info("Error adding new user {} to database: {}", emailAddress, e.getLocalizedMessage());

            sleep(SHORT_SLEEP_SECONDS);

            return "errorPage";
        }

        // Get the user's roles and mark the user as authenticated.
        Contact contact = sqlUtils.getContact(emailAddress);
        Authentication  authentication  = new UsernamePasswordAuthenticationToken(emailAddress, null, contact.getRoles());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        model.addAttribute("statusTitle", "Welcome");
        model.addAttribute("status", "You are now registered for IMPC Register Interest.");
        model.addAttribute("emailAddress", emailAddress);

        logger.info("New password successfully set for {}", emailAddress);

        return "statusPage";
    }


    @RequestMapping(value = "resetPasswordRequest", method = RequestMethod.GET)
    public String resetPasswordRequestUrl(
            ModelMap model,
            @RequestParam(value = "emailAddress", required = false) String emailAddress,
            @RequestParam(value = "status", required = false) String status)
    {

        if (emailAddress == null) {
            emailAddress = securityUtils.getPrincipal();
        }

        if ( ! emailAddress.equalsIgnoreCase("anonymousUser")) {
//            logger.info("Invalid email address '{}' created by anonymous user was rejected.", emailAddress);


            model.addAttribute("emailAddress", emailAddress);
        }

        model.addAttribute("status", "Send an e-mail to the address below to reset the password.");

        return "resetPasswordRequestPage";
    }


    @RequestMapping(value = "resetPasswordEmail", method = RequestMethod.POST)
    public String resetPasswordEmailUrl(
            ModelMap model,
            @RequestParam(value = "emailAddress", required = false) String emailAddress,
            @RequestParam(value = "emailAddressHidden", required = false) String emailAddressHidden) throws InterestException {

        String originalEmailAddress = emailAddress;

        if (emailAddress == null) {
            emailAddress = emailAddressHidden;
        }
        if (emailAddress == null) {
            emailAddress = securityUtils.getPrincipal();
        }

        if ( ! sqlUtils.isValidEmailAddress(emailAddress)) {

            logger.info("Invalid email address '{}' created by " + (originalEmailAddress == null ? "anonymous user" : originalEmailAddress) + " was rejected.", emailAddress);

            sleep(SHORT_SLEEP_SECONDS);

            return "redirect:/resetPasswordRequest?error=true";
        }

        model.addAttribute("PASSWORD_RESET_TTL_MINUTES", PASSWORD_RESET_TTL_MINUTES);
        model.addAttribute("emailAddress", emailAddress);

        // Generate and assemble email with password reset
        String token     = buildToken(emailAddress);
        String tokenLink = paHostname + paContextRoot + "/resetPassword?token=" + token;
        System.out.println("tokenLink = " + tokenLink);
        String  body    = generateResetPasswordEmail(tokenLink);
        String  subject = "Reset IMPC Register Interest password link";
        Message message = emailUtils.assembleEmail(smtpHost, smtpPort, smtpFrom, smtpReplyto, subject, body, emailAddress, true);

        // Insert add request to reset_credentials table
        ResetCredentials resetCredentials = new ResetCredentials(emailAddress, token, new Date());
        sqlUtils.updateResetCredentials(resetCredentials);

        // Send e-mail
        emailUtils.sendEmail(message);

        logger.info("Sent Reset Password email to {}", emailAddress);

        return "resetPasswordSendPage";
    }


    @RequestMapping(value = "resetPassword", method = RequestMethod.GET)
    public String resetPasswordUrlGet(ModelMap model, HttpServletRequest request) {

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
        if (dateUtils.isExpired(resetCredentials.getCreatedAt(), PASSWORD_RESET_TTL_MINUTES)) {
            model.addAttribute("title", TITLE_INVALID_TOKEN);
            model.addAttribute("error", ERR_INVALID_TOKEN);

            logger.info("Token {} has expired", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "errorPage";
        }

        // Add token to model and return "resetPassword"
        model.addAttribute("token", token);

        return "resetPasswordPage";
    }

    @RequestMapping(value = "resetPassword", method = RequestMethod.POST)
    public String resetPasswordUrlPost(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                       @RequestParam("token") String token,
                                       @RequestParam("newPassword") String newPassword,
                                       @RequestParam("repeatPassword") String repeatPassword) {
        model.addAttribute("token", token);

        // Validate the new password. Return to resetPassword page if validation fails.
        String error = validateNewPassword(newPassword, repeatPassword);
        if (!error.isEmpty()) {
            model.addAttribute("error", ERR_PASSWORD_MISMATCH);

            logger.info("Token {} not found", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "resetPasswordPage";
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

        // Update the password
        updatePassword(emailAddress, newPassword);

        logger.info("Password successfully reset for {}", emailAddress);

        // Get the user's roles and mark the user as authenticated.
        Contact contact = sqlUtils.getContact(emailAddress);
        Authentication  authentication  = new UsernamePasswordAuthenticationToken(emailAddress, null, contact.getRoles());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        model.addAttribute("statusTitle", "Password is reset");
        model.addAttribute("status", "Your password has been reset.");
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

    private String generateNewAccountEmail(String tokenLink) {

        StringBuilder body = new StringBuilder()
                .append("<html>")
                .append(EMAIL_STYLE)
                .append("<table>")
                .append("<tr>")
                .append("<td>")
                .append("Dear colleague,")
                .append("<br />")
                .append("<br />")
                .append("This e-mail was sent in response to a request to create a new IMPC Register Interest account. ")
                .append("If you made no such request, please ignore this e-mail; otherwise, please click on the link ")
                .append("below to set your IMPC Register Interest password and create your account.")
                .append("<br />")
                .append("<br />")
                .append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td class=\"button\">")
                .append("<a href=\"" + tokenLink + "\">Set password</a>")
                .append("</td>")
                .append("</tr>")
                .append("</table>")
                .append("</html>");

        return body.toString();
    }

    private String generateResetPasswordEmail(String tokenLink) {

        StringBuilder body = new StringBuilder()
                .append("<html>")
                .append(EMAIL_STYLE)
                .append("<table>")
                .append("<tr>")
                .append("<td>")
                .append("Dear colleague,")
                .append("<br />")
                .append("<br />")
                .append("This e-mail was sent in response to a request to reset your IMPC Register Interest password. ")
                .append("If you made no such request, please ignore this e-mail; otherwise, please click on the link ")
                .append("below to reset your IMPC Register Interest password.")
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

    private int updatePassword(String emailAddress, String rawPassword) {

        String password = passwordEncoder.encode(rawPassword);
        return sqlUtils.updatePassword(emailAddress, password);
    }

    private String validateNewPassword(String newPassword, String repeatPassword) {

        if (newPassword.isEmpty()) {
            return "Please specify a new password";
        }

        if (!newPassword.equals(repeatPassword)) {
            return "Passwords do not match";
        }

        return "";
    }

    @Component
    public class RIAuthenticationFailureBadCredentialsEvent implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

        private String badEmailAddress;

        @Override
        public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {

            Authentication auth = event.getAuthentication();

            failedUsername = auth.getPrincipal().toString();
            List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

            logger.info("Login failed for user {} with roles {}", badEmailAddress, StringUtils.join(roles, ", "));
        }
    }

    @Component
    public class RIAuthenticationSuccessEvent implements ApplicationListener<AuthenticationSuccessEvent> {
        @Override
        public void onApplicationEvent(AuthenticationSuccessEvent event) {

            failedUsername = null;
            Authentication auth = event.getAuthentication();

            String authenticatedUser = auth.getPrincipal().toString();
            List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

            logger.info("User {} with roles {} logged in.", authenticatedUser, StringUtils.join(StringUtils.join(auth.getAuthorities(), ", ")));
        }
    }
}