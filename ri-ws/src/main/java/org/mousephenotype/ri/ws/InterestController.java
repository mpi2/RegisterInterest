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
import org.mousephenotype.ri.core.entities.Component;
import org.mousephenotype.ri.core.entities.Contact;
import org.mousephenotype.ri.core.entities.Gene;
import org.mousephenotype.ri.core.entities.Interest;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class InterestController {

    private SqlUtils sqlUtils;

    private Map<String, Component> componentMap;

    private int DISEASE_COMPONENT_PK;
    private int GENE_COMPONENT_PK;
    private int PHENOTYPE_COMPONENT_PK;

    @PostConstruct
    public void initialise() {
    	System.out.println("initiallising interest-rs/web service--------------------------------------------------------------------------");
        componentMap = sqlUtils.getComponents();
        DISEASE_COMPONENT_PK = componentMap.get("disease").getPk();
        GENE_COMPONENT_PK = componentMap.get("gene").getPk();
        PHENOTYPE_COMPONENT_PK = componentMap.get("phenotype").getPk();
    }

    @Inject
    public InterestController(SqlUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }


    @RequestMapping(method = GET, value = "/")
    public ResponseEntity<List<Interest>> wsRoot(
    ) {

        List<Interest> list = new ArrayList<>();
        list.add(new Interest("test@ebi.ac.uk", "MGI:0000000"));
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus status = HttpStatus.OK;

        return new ResponseEntity<>(list, responseHeaders, status);
    }


    // By default, spring interprets any parameter containing a dot as an extension. In the case of the emailAddress,
    // this results in "mrelac@ebi.ac.uk" => "mrelac@ebi.ac". Adding ":.+" to the end of the parameter definition
    // tells spring not to truncate.

    @RequestMapping(method = GET, value = "/email/{emailAddress:.+}")
    public ResponseEntity<List<Interest>> getGenesForContact(
            @PathVariable("emailAddress") String emailAddress,
            @RequestParam(value = "mgiAccessionId", required = false) String mgiAccessionId
    ) {

        List<Interest> list = sqlUtils.getGenesForContact(emailAddress, mgiAccessionId);
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus status = (list.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);

        return new ResponseEntity<>(list, responseHeaders, status);
    }

    @RequestMapping(method = GET, value = "/gene/{mgiAccessionId}")
    public ResponseEntity<List<Interest>> getContactsForGene(
            @PathVariable(value = "mgiAccessionId") String mgiAccessionId,
            @RequestParam(value = "emailAddress", required = false) String emailAddress
    ) {

        List<Interest> list = sqlUtils.getContactsForGene(mgiAccessionId, emailAddress);
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus status = (list.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);

        return new ResponseEntity<>(list, responseHeaders, status);
    }

    @RequestMapping(method = POST, value = "/{emailAddress:.+}/{mgiAccessionId}")
    public ResponseEntity<Interest> register(
           @PathVariable("emailAddress")  String emailAddress,
           @PathVariable("mgiAccessionId") String mgiAccessionId
    ) {

        int authorised_contact_pk = 1;
        HttpHeaders responseHeaders = new HttpHeaders();
        String message;

        Interest interest = new Interest(emailAddress, mgiAccessionId);
        Gene gene = sqlUtils.getGene(mgiAccessionId);
        if (gene == null) {
            message = "Register contact " + emailAddress + " for gene " + mgiAccessionId + " failed: Nonexisting gene";
            sqlUtils.logWebServiceAction(authorised_contact_pk, GENE_COMPONENT_PK, null, message);
            return new ResponseEntity<>(interest, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        EmailValidator validator = EmailValidator.getInstance(false);
        if ( ! validator.isValid(emailAddress)) {
            message = "Register contact " + emailAddress + " for gene " + mgiAccessionId + " failed: malformatted email address";
            sqlUtils.logWebServiceAction(authorised_contact_pk, GENE_COMPONENT_PK, gene.getPk(), message);
            return new ResponseEntity<>(interest, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        try {

            Interest tempInterest = sqlUtils.getInterest(emailAddress, mgiAccessionId);
            if (tempInterest != null) {
                DateFormat inputDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                message = "Register contact " + emailAddress + " for gene " + mgiAccessionId + ": already registered on " + inputDateFormatter.format(tempInterest.getUpdatedAt());
                sqlUtils.logWebServiceAction(authorised_contact_pk, GENE_COMPONENT_PK, gene.getPk(), message);
                return new ResponseEntity<>(tempInterest, responseHeaders, HttpStatus.OK);
            }
            sqlUtils.insertInterest(interest);
            interest = sqlUtils.getInterest(emailAddress, mgiAccessionId);      // Update the Interest instance, which may not have primary keys, active, etc. set.
            message = "Register contact " + emailAddress + " for gene " + mgiAccessionId + ": OK";
            sqlUtils.logWebServiceAction(authorised_contact_pk, GENE_COMPONENT_PK, gene.getPk(), message);

            return new ResponseEntity<>(interest, responseHeaders, HttpStatus.OK);

        } catch (InterestException e) {

            message = "Register contact " + emailAddress + " for gene " + mgiAccessionId + " failed: " + e.getLocalizedMessage();
            sqlUtils.logWebServiceAction(authorised_contact_pk, GENE_COMPONENT_PK, gene.getPk(), message);

            return new ResponseEntity<>(interest, responseHeaders, e.getHttpStatus());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{emailAddress:.+}/{mgiAccessionId}")
    public ResponseEntity<Interest> unregister(
            @PathVariable("emailAddress") String emailAddress,
            @PathVariable("mgiAccessionId") String mgiAccessionId
    ) {

        int authorised_contact_pk = 1;
        HttpHeaders responseHeaders = new HttpHeaders();
        String message;

        Interest interest = new Interest();

        Gene gene = sqlUtils.getGene(mgiAccessionId);
        if (gene == null) {
            message = "Unregister contact " + emailAddress + " for gene " + mgiAccessionId + " failed: Nonexisting gene";
            sqlUtils.logWebServiceAction(authorised_contact_pk, GENE_COMPONENT_PK, null, message);

            return new ResponseEntity<>(interest, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        Contact contact = sqlUtils.getContact(emailAddress);
        if (contact == null) {
            message = "Unregister contact " + emailAddress + " for gene " + mgiAccessionId + " failed: Nonexisting email address";
            sqlUtils.logWebServiceAction(authorised_contact_pk, GENE_COMPONENT_PK, gene.getPk(), message);

            return new ResponseEntity<>(interest, responseHeaders, HttpStatus.BAD_REQUEST);
        }

        try {
            interest = sqlUtils.getInterest(emailAddress, mgiAccessionId);
            if (interest == null) {
                message = "Unregister contact " + emailAddress + " for gene " + mgiAccessionId + " failed: no such registration exists";
                sqlUtils.logWebServiceAction(authorised_contact_pk, GENE_COMPONENT_PK, gene.getPk(), message);
                return new ResponseEntity<>(interest, responseHeaders, HttpStatus.NOT_FOUND);
            }

            sqlUtils.removeInterest(interest);
            message = "Unregister contact " + emailAddress + " for gene " + mgiAccessionId + ": OK";
            sqlUtils.logWebServiceAction(authorised_contact_pk, GENE_COMPONENT_PK, gene.getPk(), message);

            return new ResponseEntity<>(interest, responseHeaders, HttpStatus.OK);

        } catch (InterestException e) {

            message = "Unregister contact " + emailAddress + " for gene " + mgiAccessionId + " failed: " + e.getLocalizedMessage();
            sqlUtils.logWebServiceAction(authorised_contact_pk, GENE_COMPONENT_PK, gene.getPk(), message);

            return new ResponseEntity<>(interest, responseHeaders, e.getHttpStatus());
        }
    }
}