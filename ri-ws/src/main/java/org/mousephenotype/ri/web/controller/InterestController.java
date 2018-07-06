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

import org.mousephenotype.ri.core.SecurityUtils;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.Gene;
import org.mousephenotype.ri.core.entities.Summary;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.mousephenotype.ri.reports.ContactGeneReport;
import org.mousephenotype.ri.reports.support.MpCSVWriter;
import org.mousephenotype.ri.reports.support.ReportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.springframework.web.bind.annotation.RequestMethod.*;


@RestController
public class InterestController implements ErrorController {

    private final Logger        logger        = LoggerFactory.getLogger(this.getClass());
    private       SecurityUtils securityUtils = new SecurityUtils();

    private SqlUtils sqlUtils;

    private static final String ERROR_PATH = "/error";

    @Inject
    public InterestController(SqlUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }


    @RequestMapping(method = GET, value = "/api/summary")
    public ResponseEntity<Summary> summaryGetUrl() {

        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus  status          = HttpStatus.OK;
        Summary     summary;

        summary = sqlUtils.getSummary(securityUtils.getPrincipal());

        return new ResponseEntity<>(summary, responseHeaders, status);
    }

    /**
     * Register Interest in Gene endpoint
     *
     * @param geneAccessionId
     * @return message if an error or warning occurred; an empty string otherwise
     */
    @RequestMapping(method = POST, value = "/api/registration/gene/{acc}")
    public ResponseEntity<String> registrationGenePostUrl(
            @PathVariable("acc") String geneAccessionId
    ) {
        String      message;
        HttpHeaders responseHeaders = new HttpHeaders();

        Gene gene = sqlUtils.getGene(geneAccessionId);
        if (gene == null) {
            message = "gene " + geneAccessionId + " does not exist.";
            logger.warn(message);
            return new ResponseEntity<>(message, responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {

            sqlUtils.registerGene(securityUtils.getPrincipal(), geneAccessionId);

        } catch (InterestException e) {

            logger.warn(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), e.getHttpStatus());
        }

        return new ResponseEntity<>("", responseHeaders, HttpStatus.OK);
    }

    /**
     * Unregister Interest in Gene endpoint
     *
     * @param geneAccessionId
     *
     * @return message if an error or warning occurred; an empty string otherwise
     */
    @RequestMapping(method = DELETE, value = "/api/geneUnregistration/{acc}")
    public ResponseEntity<String> registrationGeneDeleteUrl(
            @PathVariable("acc") String geneAccessionId
    ) {
        String      message;
        HttpHeaders responseHeaders = new HttpHeaders();

        Gene gene = sqlUtils.getGene(geneAccessionId);
        if (gene == null) {
            message = "doGeneUnregistration(): gene " + geneAccessionId + " does not exist.";
            logger.error(message);
            return new ResponseEntity<>(message, responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {

            sqlUtils.registerGene(securityUtils.getPrincipal(), geneAccessionId);

        } catch (InterestException e) {

            return new ResponseEntity<>(e.getLocalizedMessage(), e.getHttpStatus());
        }

        return new ResponseEntity<>("", responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(method = GET, value = "/admin/reports/ContactGene")
    public void getContactGeneReport(HttpServletResponse response) throws IOException, ReportException {

        response.setContentType("text/csv; charset=utf-8");
        PrintWriter writer = response.getWriter();
        MpCSVWriter csvWriter = new MpCSVWriter(writer);
        ContactGeneReport report = new ContactGeneReport(sqlUtils);
        report.run(new String[0], csvWriter);

        csvWriter.close();
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }


    @RequestMapping(value = ERROR_PATH)
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


    // PRIVATE METHODS


    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
    }
}