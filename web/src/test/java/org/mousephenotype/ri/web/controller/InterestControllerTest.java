package org.mousephenotype.ri.web.controller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.BaseTest;
import org.mousephenotype.ri.core.entities.Gene;
import org.mousephenotype.ri.core.entities.Summary;
import org.mousephenotype.ri.web.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by mrelac on 16/08/2018.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
//@WebAppConfiguration
@SpringBootTest
public class InterestControllerTest extends BaseTest {

    @Autowired
    private InterestController interestController;


    private final String registeredGeneAccessionId = "MGI:103576";
    private final String unregisteredGeneAccessionId = "MGI:2444824";


    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }


    @Test
    public void apiSummaryNoUser() {
        ResponseEntity<Summary> response = interestController.apiSummary();
        Summary summary = response.getBody();
        Assert.assertTrue(response.getStatusCode() == HttpStatus.OK);
        Assert.assertEquals("anonymous", summary.getEmailAddress());
        Assert.assertTrue(summary.getGenes().isEmpty());
    }


    @Test
    @WithMockUser
    public void apiSummaryUnauthenticatedUser() {
        ResponseEntity<Summary> response = interestController.apiSummary();
        Summary summary = response.getBody();
        Assert.assertTrue(response.getStatusCode() == HttpStatus.OK);
        Assert.assertEquals("user", summary.getEmailAddress());
        Assert.assertTrue(summary.getGenes().isEmpty());
    }


    @Test
    @WithMockUser("user1@ebi.ac.uk")
    public void apiSummaryTest() {
        final List<String> expectedGeneAccessionIds = new ArrayList<>(Arrays.asList(new String[] { "MGI:103576", "MGI:1919199", "MGI:2443658" }));
        ResponseEntity<Summary> response = interestController.apiSummary();
        Summary summary = response.getBody();
        Assert.assertTrue(response.getStatusCode() == HttpStatus.OK);
        Assert.assertEquals(3, summary.getGenes().size());
        List<Gene>          genes         = summary.getGenes();
        for (Gene gene : genes) {
            Assert.assertTrue(expectedGeneAccessionIds.contains(gene.getMgiAccessionId()));
        }
    }

    @Test
    @WithMockUser("user1@ebi.ac.uk")
    public void apiSummaryList() {
        final List<String> expectedGeneAccessionIds = new ArrayList<>(Arrays.asList(new String[] { "MGI:103576", "MGI:1919199", "MGI:2443658" }));
        ResponseEntity<Map<String, List<String>>> response = interestController.apiSummaryList();
        Assert.assertTrue(response.getStatusCode() == HttpStatus.OK);
        Assert.assertEquals(1, response.getBody().size());
        List<String> actualGeneAccessionIds = response.getBody().get("geneAccessionIds");
        Assert.assertEquals(3, actualGeneAccessionIds.size());
        for (String geneAccessionId : expectedGeneAccessionIds) {
            Assert.assertTrue(actualGeneAccessionIds.contains(geneAccessionId));
        }
    }

    @Test
    @WithMockUser("user1@ebi.ac.uk")
    public void apiRegistrationGeneInfo() {
        ResponseEntity<String> response = interestController.apiRegistrationGeneInfo(registeredGeneAccessionId);
        Assert.assertTrue(response.getBody().equals("true"));
        response = interestController.apiRegistrationGeneInfo(unregisteredGeneAccessionId);
        Assert.assertTrue(response.getBody().equals("false"));
    }

    @Test
    public void apiRolesNoUser() {
        List<String> response = interestController.apiRoles();
        Assert.assertTrue(response.isEmpty());
    }

    @Test
    @WithAnonymousUser
    public void apiRolesAnonymousUser() {
        List<String> response = interestController.apiRoles();
        Assert.assertEquals(1, response.size());
        Assert.assertTrue(response.get(0).equals("ROLE_ANONYMOUS"));
    }

    @Test
    @WithMockUser("user1@ebi.ac.uk")
    public void apiRolesAuthenticatedUser() {
        List<String> response = interestController.apiRoles();
        Assert.assertEquals(1, response.size());
        Assert.assertTrue(response.get(0).equals("ROLE_USER"));
    }

    @Test
    public void apiUnregistrationGeneNoUser() {
        ResponseEntity<String> response = interestController.apiUnregistrationGene(registeredGeneAccessionId);
        Assert.assertEquals(404, response.getStatusCodeValue());
        Assert.assertTrue(response.getBody().equals("Invalid contact anonymous."));
    }

    @Test
    @WithAnonymousUser
    public void apiUnregistrationGeneAnonymousUser() {
        ResponseEntity<String> response = interestController.apiUnregistrationGene(registeredGeneAccessionId);
        Assert.assertEquals(404, response.getStatusCodeValue());
        Assert.assertTrue(response.getBody().equals("Invalid contact anonymous."));
    }

    @Test
    @WithMockUser("user1@ebi.ac.uk")
    public void apiUnregistrationGeneAuthenticatedUserRegisteredGene() {
        ResponseEntity<String> response = interestController.apiUnregistrationGene(registeredGeneAccessionId);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertTrue(response.getBody().isEmpty());
    }

    @Test
    @WithMockUser("user1@ebi.ac.uk")
    public void apiUnregistrationGeneAuthenticatedUserUnregisteredGene() {
        final String expectedBody = "Contact user1@ebi.ac.uk is not registered for gene MGI:2444824.";
        ResponseEntity<String> response = interestController.apiUnregistrationGene(unregisteredGeneAccessionId);
        Assert.assertEquals(404, response.getStatusCodeValue());
        Assert.assertEquals(expectedBody, response.getBody());
    }

    @Test
    @WithAnonymousUser
    public void apiRegistrationGeneAnonymousUser() {
        ResponseEntity<String> response = interestController.apiRegistrationGene(unregisteredGeneAccessionId);
        Assert.assertEquals(404, response.getStatusCodeValue());
        Assert.assertTrue(response.getBody().equals("Invalid contact anonymous."));
    }

    @Test
    @WithMockUser("user1@ebi.ac.uk")
    public void apiRegistrationGeneAuthenticatedUserRegisteredGene() {
        final String expectedBody = "Contact user1@ebi.ac.uk is already registered for gene MGI:103576.";
        ResponseEntity<String> response = interestController.apiRegistrationGene(registeredGeneAccessionId);
        Assert.assertEquals(400, response.getStatusCodeValue());
        Assert.assertEquals(expectedBody, response.getBody());
    }

    @Test
    @WithMockUser("user1@ebi.ac.uk")
    public void apiRegistrationGeneAuthenticatedUserUnregisteredGene() {
        ResponseEntity<String> response = interestController.apiRegistrationGene(unregisteredGeneAccessionId);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertTrue(response.getBody().isEmpty());
    }
}