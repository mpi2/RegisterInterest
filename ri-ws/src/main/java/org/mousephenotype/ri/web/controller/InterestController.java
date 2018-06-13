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

package org.mousephenotype.ri.web.controller;

import org.apache.commons.validator.routines.EmailValidator;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.GeneContact;
import org.mousephenotype.ri.core.entities.Interest;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.mousephenotype.ri.reports.GeneContactReport;
import org.mousephenotype.ri.reports.support.MpCSVWriter;
import org.mousephenotype.ri.reports.support.ReportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class InterestController implements ErrorController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private SqlUtils sqlUtils;

    private static final String PATH = "/error";

    @Inject
    public InterestController(SqlUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }

    public final static String INTEREST_DISEASE = "disease";
    public final static String INTEREST_GENE = "gene";
    public final static String INTEREST_PHENOTYPE = "phenotype";


    @RequestMapping(method = GET, value = "/contacts")
    public ResponseEntity<List<Interest>> getContacts(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = INTEREST_GENE, required = false) String geneAccessionId
    ) {

        List<Interest> list = sqlUtils.getInterests(email, type, geneAccessionId);
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus status = HttpStatus.OK;

        return new ResponseEntity<>(list, responseHeaders, status);
    }
    
    

    @RequestMapping(method = POST, value = "/contacts")
    public ResponseEntity<String> register(

            @RequestParam(value = "email", required = true) String email,
            @RequestParam(value = "type", required = true) String type,
            @RequestParam(value = "gene", required = true) String geneAccessionId
    ) {

        HttpHeaders responseHeaders = new HttpHeaders();
        List<Interest> interests;
        String message = "";
        Date now = new Date();

        if (( ! type.equals(INTEREST_GENE)) && (! type.equals("disease")) && ( ! type.equals("phenotype"))) {
            return new ResponseEntity<>("Invalid type. Expected one of: gene, disease, or phenotype", responseHeaders, HttpStatus.BAD_REQUEST);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String invoker = (auth == null ? "Unknown" : auth.getName());

        // Validate the email address.
        EmailValidator validator = EmailValidator.getInstance(false);
        if ( ! validator.isValid(email)) {
            message = "Register contact " + email + " for gene " + geneAccessionId + " failed: malformatted email address";
            sqlUtils.logSendAction(invoker, null, null, message);
            return new ResponseEntity<>(message, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        if (type.equals(INTEREST_GENE)) {
            try {

                GeneContact geneContact = sqlUtils.getGeneContact(geneAccessionId, email);
                if ((geneContact == null) || (geneContact.getActive() == 0)) {
                    int geneContactActive = 1;
                    sqlUtils.insertOrUpdateInterestGene(invoker, geneAccessionId, email, now, geneContactActive, now);
                } else {
                    message = "Register contact " + email + " for gene " + geneAccessionId + ": contact is already registered for that gene.";
                    sqlUtils.logSendAction(invoker, null, null, message);
                    return new ResponseEntity<>(message, responseHeaders, HttpStatus.OK);
                }

                interests = sqlUtils.getInterests(email, INTEREST_GENE, geneAccessionId);

            } catch (InterestException e) {

                return new ResponseEntity<>(e.getLocalizedMessage(), responseHeaders, e.getHttpStatus());
            }

            message = "Register contact " + email + " for gene " + geneAccessionId + ": OK";
            if ( ! interests.isEmpty()) {
                sqlUtils.logSendAction(invoker, interests.get(0).getGenes().get(0).getPk(), interests.get(0).getContact().getPk(), message);
            }
        }

        return new ResponseEntity<>(message, responseHeaders, HttpStatus.OK);
    }



    @RequestMapping(method = DELETE, value = "/contacts")
    public ResponseEntity<String> unregister(

            @RequestParam(value = "email", required = true) String email,
            @RequestParam(value = "type", required = true) String type,
            @RequestParam(value = "gene", required = true) String geneAccessionId
    ) {

        HttpHeaders responseHeaders = new HttpHeaders();
        GeneContact geneContact;
        String message = "";

        if (( ! type.equals(INTEREST_GENE)) && (! type.equals("disease")) && ( ! type.equals("phenotype"))) {
            return new ResponseEntity<>("Invalid type. Expected one of: gene, disease, or phenotype", responseHeaders, HttpStatus.BAD_REQUEST);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String invoker = (auth == null ? "Unknown" : auth.getName());

        if (type.equals(INTEREST_GENE)) {
            try {

                geneContact = sqlUtils.getGeneContact(geneAccessionId, email);
                if ((geneContact == null) || (geneContact.getActive() == 0)) {
                    message = "Unregister contact " + email + " for gene " + geneAccessionId + " failed: no such active registration exists";
                    throw new InterestException(message, HttpStatus.NOT_FOUND);
                }

                int genePk = geneContact.getGenePk();
                int contactPk = geneContact.getContactPk();

                sqlUtils.insertOrUpdateGeneContact(genePk, contactPk, -1, null);

                message = "Unregister contact scheduled for " + email + " for gene " + geneAccessionId + ": OK";
                sqlUtils.logSendAction(invoker, genePk, contactPk, message);

            } catch (InterestException e) {

                sqlUtils.logSendAction(invoker, null, null, message);

                return new ResponseEntity<>(e.getLocalizedMessage(), responseHeaders, e.getHttpStatus());
            }
        }

        return new ResponseEntity<>(message, responseHeaders, HttpStatus.OK);
    }


    @RequestMapping(method = GET, value = "/reports/GeneContact")
    public void getGeneContactReport(HttpServletResponse response) throws IOException, ReportException {

        response.setContentType("text/csv; charset=utf-8");
        PrintWriter writer = response.getWriter();
        MpCSVWriter csvWriter = new MpCSVWriter(writer);
        GeneContactReport report = new GeneContactReport(sqlUtils);
        report.run(new String[0], csvWriter);

        csvWriter.close();
    }

    @RequestMapping(value = PATH)
    public String handleErrors(HttpServletRequest httpRequest) {

        int httpErrorCode = getErrorCode(httpRequest);
        String errorMsg = "Http Error Code: " + Integer.toString(httpErrorCode) + ".";

        switch (httpErrorCode) {
            case 400: {
                errorMsg += " Bad Request";
                break;
            }
            case 401: {
                errorMsg += " Unauthorized";
                break;
            }
            case 404: {
                errorMsg += " Resource not found";
                break;
            }
            case 500: {
                errorMsg += " Internal Server Error";
                break;
            }
        }

        return errorMsg;
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}