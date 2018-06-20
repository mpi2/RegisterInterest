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
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.ContactExtended;
import org.mousephenotype.ri.core.entities.ResetCredentials;
import org.mousephenotype.ri.core.entities.Summary;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class SummaryController {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private EmailUtils emailUtils = new EmailUtils();
    private DateUtils dateUtils = new DateUtils();


    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SqlUtils sqlUtils;

    @Autowired
    private String paHostname;

    @Autowired
    private String paContextRoot;

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

    private final int PASSWORD_RESET_TTL_MINUTES = 10;


    @Resource(name = "globalConfiguration")
    private Map<String, String> config;



    @RequestMapping(value = "/Access_Denied", method = RequestMethod.GET)
    public String accessDeniedPage(ModelMap model) {
        model.addAttribute("user", getPrincipal());
        return "accessDenied";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(HttpServletRequest request) {

        return "login";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:summary";
    }






    public final String SUMMARY = "summary";
    @RequestMapping(value = {SUMMARY }, method = RequestMethod.GET)
    public String summary(HttpServletRequest request, ModelMap model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String user = getPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("uri", request.getRequestURI());
        model.addAttribute("roles", auth.getAuthorities());
        model.addAllAttributes(config);

        System.out.println("user/principal = " + getPrincipal());
        System.out.println("uri = " + request.getRequestURI());
        System.out.println("url = " + request.getRequestURL());
        System.out.println("roles = " + StringUtils.join(auth.getAuthorities(), ", "));

        // FIXME Restrict web service by ROLE and URL. Replace embedded Summary.Gene class with real Gene class.
        Summary[]  summary = restTemplate.getForObject(
                "http://localhost:8081/data/interest/contacts?email=" + user , Summary[].class
        );

        List<Summary.Gene> genes = summary[0].getGenes();

        genes.sort(Comparator.comparing(Summary.Gene::getSymbol));

        model.addAttribute(summary);

        return SUMMARY;
    }






    @RequestMapping(value = "resetPasswordRequest", method = RequestMethod.GET)
    public String resetPasswordRequestGet(ModelMap model, HttpServletRequest request, HttpServletResponse response) {

        String user = getPrincipal();
        if ( ! user.equalsIgnoreCase("anonymousUser")) {
            model.addAttribute("user", user);
            model.addAttribute("emailAddress", user);

        }

        return "resetPasswordRequest";
    }


    @RequestMapping(value = "resetPasswordEmail", method = RequestMethod.POST)
    public String resetPasswordEmail(
            ModelMap model,
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam ("emailAddress") String emailAddress
    ) throws InterestException {


        model.addAttribute("PASSWORD_RESET_TTL_MINUTES", PASSWORD_RESET_TTL_MINUTES);
        model.addAttribute("emailAddress", emailAddress);

        if ( ! sqlUtils.isValidEmailAddress(emailAddress)) {
            return "resetPasswordRequest";
        }

        // Generate and assemble email with password reset
        String token = buildToken(emailAddress);
        String tokenLink = buildTokenLink(emailAddress, token);
        System.out.println("tokenLink = " + tokenLink);
        String body = generateResetPasswordEmail(emailAddress, tokenLink);
        String subject = "Reset IMPC Register Interest password link";
        Message message = emailUtils.assembleEmail(smtpHost, smtpPort, smtpFrom, smtpReplyto, subject, body, emailAddress, true);

        // Insert add request to reset_credentials table
        ResetCredentials resetCredentials = new ResetCredentials(emailAddress, token, new Date());
        sqlUtils.updateResetCredentials(resetCredentials);

        // Send e-mail
        emailUtils.sendEmail(message);

        return "resetPasswordSent";
    }


    @RequestMapping(value = "resetPassword", method = RequestMethod.GET)
    public String resetPasswordGet(ModelMap model, HttpServletRequest request, HttpServletResponse response) {

        // Parse out query string for token value.
        String token = getTokenFromQueryString(request.getQueryString());

        // Look up user from reset_credentials table
        ResetCredentials resetCredentials = sqlUtils.getResetCredentials(token);

        // If not found, return to invalidToken page.
        if (resetCredentials == null) {
            return "invalidToken";
        }

        // If token has expired, return to invalidToken page.
        if (dateUtils.isExpired(resetCredentials.getCreatedAt(), PASSWORD_RESET_TTL_MINUTES)) {
            return "invalidToken";
        }

        // Add token to model and return "resetPassword"
        model.addAttribute("token", token);

        return "resetPassword";
    }


    @RequestMapping(value = "resetPassword", method = RequestMethod.POST)
    public String resetPasswordPost(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                @RequestParam ("token") String token,
                                @RequestParam ("newPassword") String newPassword,
                                @RequestParam ("repeatPassword") String repeatPassword)
    {
        // Validate the new password. Return to resetPassword page if validation fails.
        String error = validateNewPassword(newPassword, repeatPassword);
        if ( ! error.isEmpty()) {
            model.addAttribute("error", error);
            return "resetPassword";
        }

        // Look up user from reset_credentials table
        ResetCredentials resetCredentials = sqlUtils.getResetCredentials(token);

        // If not found, return to invalidToken page.
        if (resetCredentials == null) {
            return "invalidToken";
        }

        String emailAddress = resetCredentials.getAddress();

        // Update the password
        updatePassword(emailAddress, newPassword);

        // Get the user's roles and mark the user as authenticated.
        ContactExtended contactExtended = sqlUtils.getContactExtended(emailAddress);
        Authentication authentication = new UsernamePasswordAuthenticationToken(emailAddress, null, contactExtended.getRoles());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/summary";
    }


    // PRIVATE METHODS


    /**
     * Build and return a unique hash token for this email address
     * @param emailAddress
     * @return a unique hash token for this email address
     */
    private String buildToken(String emailAddress) {

        String token;

        SecureRandom random = new SecureRandom();
        String randomDouble = Double.toString(random.nextDouble());

        token = DigestUtils.sha256Hex(randomDouble + emailAddress);

        return token;
    }

    /**
     * return a hyperlink containing the token suitable for sending to the contact asking for password reset
     * @param emailAddress
     * @param token
     * @return
     * @throws InterestException
     */
    private String buildTokenLink(String emailAddress, String token) throws InterestException {

        return paHostname + paContextRoot + "/resetPassword?token=" + token;
    }

    private String generateResetPasswordEmail(String emailAddress, String tokenLink) {

        String style = new StringBuilder()
                .append(    "<style>")
                .append(    ".button {")
                .append(       "background-color: #286090;")
                .append(       "border-color: #204d74;")
                .append(       "border-radius: 5px;")
                .append(       "text-align: center;")
                .append(       "padding: 10px;")
                .append(     "}")
                .append(    ".button a {")
                .append(       "color: #ffffff;")
                .append(       "display: block;")
                .append(       "font-size: 14px;")
                .append(       "text-decoration: none;")
                .append(     "}")
                .append(    "</style>").toString();

        StringBuilder body = new StringBuilder();

        body
                .append("<html>")
                .append(  style)
                .append(  "<table>")
                .append(    "<tr>")
                .append(      "<td>")
                .append(        "Dear colleague,")
                .append(        "<br />")
                .append(        "<br />")
                .append(        "This e-mail was sent in response to a request to reset your IMPC Register Interest password. ")
                .append(        "If you made no such request, please ignore this e-mail; otherwise, please click on the link ")
                .append(        "below to reset your IMPC Register Interest password.")
                .append(        "<br />")
                .append(        "<br />")
                .append(      "</td>")
                .append(    "</tr>")
                .append(    "<tr>")
                .append(      "<td class=\"button\">")
                .append(        "<a href=\"" + tokenLink + "\">Reset password</a>")
                .append(      "</td>")
                .append(    "</tr>")
                .append(  "</table>")
                .append("</html>");

        return body.toString();
    }

    private String getPrincipal(){
        String userName;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails)principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }

    private String getReferer(HttpServletRequest request) {

        String referer = request.getHeader("referer");
        URL url;

        try {

            url = new URL(referer);
            return url.getPath().replace(request.getContextPath() + "/", "");

        } catch (Exception e) {

        }

        return "";
    }

    private String getTokenFromQueryString(String queryString) {

        if ((queryString == null) || (queryString.isEmpty())) {
            return "";
        }

        String[] pieces = StringUtils.split(queryString,"=");
        if ((pieces.length != 2) && ( ! pieces[0].equals("token"))) {
            return "";
        }

        return pieces[1];
    }

    private int updatePassword(String emailAddress, String rawPassword) {

        String password = passwordEncoder.encode(rawPassword);
        return sqlUtils.updatePassword(emailAddress, password);
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