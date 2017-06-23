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

package org.mousephenotype.ri.ws;

import org.apache.commons.validator.routines.EmailValidator;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.GeneContact;
import org.mousephenotype.ri.core.entities.Interest;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class InterestController {

    private SqlUtils sqlUtils;

    @Inject
    public InterestController(SqlUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }

    public final static String INTEREST_DISEASE = "disease";
    public final static String INTEREST_GENE = "gene";
    public final static String INTEREST_PHENOTYPE = "phenotype";

    @RequestMapping(method = GET, value = "/")
    public ResponseEntity<List<String>> apiDocs(
    ) {

        List<String> list = new ArrayList<>();
        list.add("API docs go here.");
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus status = HttpStatus.OK;

        return new ResponseEntity<>(list, responseHeaders, status);
    }

    
    
    @RequestMapping(method = GET, value = "/contacts")
    public ResponseEntity<List<Interest>> getContacts(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = INTEREST_GENE, required = false) String gene
    ) {

        List<Interest> list = sqlUtils.getInterests(email, type, gene);
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus status = HttpStatus.OK;

        return new ResponseEntity<>(list, responseHeaders, status);
    }
    
    

    @RequestMapping(method = POST, value = "/contacts")
    public ResponseEntity<String> register(

            @RequestParam(value = "email", required = true) String email,
            @RequestParam(value = "type", required = true) String type,
            @RequestParam(value = "gene", required = true) String gene
    ) {

        HttpHeaders responseHeaders = new HttpHeaders();
        List<Interest> interests;
        String message = "";

        if (( ! type.equals(INTEREST_GENE)) && (! type.equals("disease")) && ( ! type.equals("phenotype"))) {
            return new ResponseEntity<>("Invalid type. Expected one of: gene, disease, or phenotype", responseHeaders, HttpStatus.BAD_REQUEST);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String invoker = (auth == null ? "Unknown" : auth.getName());

        // Validate the email address.
        EmailValidator validator = EmailValidator.getInstance(false);
        if ( ! validator.isValid(email)) {
            message = "Register contact " + email + " for gene " + gene + " failed: malformatted email address";
            sqlUtils.logWebServiceAction(invoker, null, null, message);
            return new ResponseEntity<>(message, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        if (type.equals(INTEREST_GENE)) {
            try {

                int count = sqlUtils.insertInterestGene(invoker, gene, email);
                if (count == 0) {
                    DateFormat inputDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    message = "Register contact " + email + " for gene " + gene + ": contact is already registered for that gene.";
                    sqlUtils.logWebServiceAction(invoker, null, null, message);
                    return new ResponseEntity<>(message, responseHeaders, HttpStatus.OK);
                }

                interests = sqlUtils.getInterests(email, INTEREST_GENE, gene);

            } catch (InterestException e) {

                return new ResponseEntity<>(e.getLocalizedMessage(), responseHeaders, e.getHttpStatus());
            }

            message = "Register contact " + email + " for gene " + gene + ": OK";
            if ( ! interests.isEmpty()) {
                sqlUtils.logWebServiceAction(invoker, interests.get(0).getGenes().get(0).getPk(), interests.get(0).getContact().getPk(), message);
            }
        }

        return new ResponseEntity<>(message, responseHeaders, HttpStatus.OK);
    }



    @RequestMapping(method = DELETE, value = "/contacts")
    public ResponseEntity<String> unregister(

            @RequestParam(value = "email", required = true) String email,
            @RequestParam(value = "type", required = true) String type,
            @RequestParam(value = "gene", required = true) String gene
    ) {

        HttpHeaders responseHeaders = new HttpHeaders();
        GeneContact gc;
        String message = "";

        if (( ! type.equals(INTEREST_GENE)) && (! type.equals("disease")) && ( ! type.equals("phenotype"))) {
            return new ResponseEntity<>("Invalid type. Expected one of: gene, disease, or phenotype", responseHeaders, HttpStatus.BAD_REQUEST);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String invoker = (auth == null ? "Unknown" : auth.getName());

        if (type.equals(INTEREST_GENE)) {
            try {

                gc = sqlUtils.getGeneContact(gene, email, 1);
                if (gc == null) {
                    message = "Unregister contact " + email + " for gene " + gene + " failed: no such active registration exists";
                    throw new InterestException(message, HttpStatus.NOT_FOUND);
                }

                int genePk = gc.getGenePk();
                int contactPk = gc.getContactPk();

                sqlUtils.removeInterestGene(gc);
                message = "Unregister contact " + email + " for gene " + gene + ": OK";
                sqlUtils.logWebServiceAction(invoker, genePk, contactPk, message);

            } catch (InterestException e) {

                sqlUtils.logWebServiceAction(invoker, null, null, message);

                return new ResponseEntity<>(e.getLocalizedMessage(), responseHeaders, e.getHttpStatus());
            }
        }

        return new ResponseEntity<>(message, responseHeaders, HttpStatus.OK);
    }
}