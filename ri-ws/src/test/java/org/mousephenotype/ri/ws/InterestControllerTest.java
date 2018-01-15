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

package org.mousephenotype.ri.ws;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.ws.config.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by mrelac on 02/05/2017.
 *
 * NOTE: Here is a good example of how to re-execute the database load script:
 *
 *      Resource r = context.getResource("classpath:sql/h2/generate-data.sql");
 *      ScriptUtils.executeSqlScript(ds.getConnection(), r);
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = TestConfig.class)
@ComponentScan("org.mousephenotype.ri.ws")
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/ri.test.properties")
public class InterestControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;


    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @After
    public void tearDown() throws Exception {
    }

    // Test for malformed / nonexisting email

    @Test
    public void queryEmailWithNonexistingEmail() throws Exception {

        String url = "/contacts?email=junk";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.comparesEqualTo(0)))
        ;
    }

    @Test
    public void queryEmailWithNonexistingEmailAndGene() throws Exception {

        String url = "/contacts?email=junk&gene=junk";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.comparesEqualTo(0)))
        ;
    }


    // Test for existing email, nonexisting gene

    @Test
    public void queryEmailWithEmailAndNonexistentGene() throws Exception {

        String url = "/contacts?email=user1@ebi.ac.uk&gene=junk";
        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(jsonPath("$.length()", Matchers.comparesEqualTo(0)))
        ;
    }


    // Test for existing email, existing gene(s)

    @Test
    public void queryEmailWithEmailExpectMultipleGenes() throws Exception {

        String url = "/contacts?email=user1@ebi.ac.uk&type=gene";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contact.address", Matchers.comparesEqualTo("user1@ebi.ac.uk")))
                .andExpect(jsonPath("$[0].contact.active", Matchers.comparesEqualTo(true)))

                .andExpect(jsonPath("$[0].genes[0].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000010")))
                .andExpect(jsonPath("$[0].genes[0].symbol", Matchers.comparesEqualTo("gene-10")))

                .andExpect(jsonPath("$[0].genes[1].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000020")))
                .andExpect(jsonPath("$[0].genes[1].symbol", Matchers.comparesEqualTo("gene-20")))

                .andExpect(jsonPath("$[0].genes[2].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000030")))
                .andExpect(jsonPath("$[0].genes[2].symbol", Matchers.comparesEqualTo("gene-30")))
        ;
    }


    // Test for invalid type

    @Test
    public void queryWithInvalidType() throws Exception {

        String url = "/contacts?email=user1@ebi.ac.uk&type=gene&type=junk";

        this.mockMvc.perform(
                get(url)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.comparesEqualTo(0)))
        ;
    }

    @Test
    public void queryEmailWithEmailAndGeneExpectSingleGene() throws Exception {

        String url = "/contacts?email=user1@ebi.ac.uk&gene=MGI:0000030";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.comparesEqualTo(1)))
                .andExpect(jsonPath("$[0].contact.address", Matchers.comparesEqualTo("user1@ebi.ac.uk")))
                .andExpect(jsonPath("$[0].contact.active", Matchers.comparesEqualTo(true)))

                .andExpect(jsonPath("$[0].genes[0].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000030")))
                .andExpect(jsonPath("$[0].genes[0].symbol", Matchers.comparesEqualTo("gene-30")))
        ;
    }

    @Test
    public void testGetGenes() throws Exception {

        String url = "/contacts";

        this.mockMvc.perform(
                get(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.comparesEqualTo(3)))
                .andExpect(jsonPath("$[0].contact.address", Matchers.comparesEqualTo("user1@ebi.ac.uk")))
                .andExpect(jsonPath("$[0].contact.active", Matchers.comparesEqualTo(true)))

                .andExpect(jsonPath("$[0].genes.length()", Matchers.comparesEqualTo(3)))
                .andExpect(jsonPath("$[0].genes[0].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000010")))
                .andExpect(jsonPath("$[0].genes[0].symbol", Matchers.comparesEqualTo("gene-10")))

                .andExpect(jsonPath("$[0].genes[1].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000020")))
                .andExpect(jsonPath("$[0].genes[1].symbol", Matchers.comparesEqualTo("gene-20")))

                .andExpect(jsonPath("$[0].genes[2].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000030")))
                .andExpect(jsonPath("$[0].genes[2].symbol", Matchers.comparesEqualTo("gene-30")))


                .andExpect(jsonPath("$[1].genes.length()", Matchers.comparesEqualTo(1)))
                .andExpect(jsonPath("$[1].contact.address", Matchers.comparesEqualTo("user2@ebi.ac.uk")))
                .andExpect(jsonPath("$[1].contact.active", Matchers.comparesEqualTo(true)))

                .andExpect(jsonPath("$[1].genes.length()", Matchers.comparesEqualTo(1)))
                .andExpect(jsonPath("$[1].genes[0].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000010")))
                .andExpect(jsonPath("$[1].genes[0].symbol", Matchers.comparesEqualTo("gene-10")))


                .andExpect(jsonPath("$[2].genes.length()", Matchers.comparesEqualTo(1)))
                .andExpect(jsonPath("$[2].contact.address", Matchers.comparesEqualTo("user3@ebi.ac.uk")))
                .andExpect(jsonPath("$[2].contact.active", Matchers.comparesEqualTo(true)))

                .andExpect(jsonPath("$[2].genes.length()", Matchers.comparesEqualTo(1)))
                .andExpect(jsonPath("$[2].genes[0].mgiAccessionId", Matchers.comparesEqualTo("MGI:0000010")))
                .andExpect(jsonPath("$[2].genes[0].symbol", Matchers.comparesEqualTo("gene-10")))
        ;
    }



    // REGISTER (POST)


    @Test
    public void registerEmailOnly() throws Exception {

        String url = "/contacts?user1@ebi.ac.uk";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("Required String parameter 'email' is not present"))
        ;
    }

    @Test
    public void registerGeneOnly() throws Exception {

        String url = "/contacts?gene=MGI:0000050";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("Required String parameter 'email' is not present"))
        ;
    }

    @Test
    public void registerMalformedEmailAndGene() throws Exception {

        String url = "/contacts?email=junk&gene=MGI:0000060&type=gene";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", Matchers.comparesEqualTo("Register contact junk for gene MGI:0000060 failed: malformatted email address")))
        ;
    }

    @Test
    public void registerExistingEmailAndNonexistingGene() throws Exception {

        String url = "/contacts?email=user1@ebi.ac.uk&gene=junk&type=gene";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", Matchers.comparesEqualTo("Register contact user1@ebi.ac.uk for gene junk failed: Nonexisting gene")))
        ;
    }

    @Test
    public void registerNonexistingEmailAndNonexistingGene() throws Exception {

        String url = "/contacts?email=newuser@ebi.ac.uk&gene=junk&type=gene";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", Matchers.comparesEqualTo("Register contact newuser@ebi.ac.uk for gene junk failed: Nonexisting gene")))
        ;
    }

    @Test
    public void registerExistingEmailAndNonexistingGeneWithBadType() throws Exception {

        String url = "/contacts?email=newuser@ebi.ac.uk&gene=gene-10&type=junk";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", Matchers.comparesEqualTo("Invalid type. Expected one of: gene, disease, or phenotype")))
        ;
    }

    @Test
    public void registerExistingEmailAndExistingUnregisteredGeneWithoutType() throws Exception {

        String url = "/contacts?email=user2@ebi.ac.uk/gene=MGI:0000020";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("Required String parameter 'type' is not present"))
        ;
    }


    // THESE SHOULD RETURN ROWS AND HAVE STATUS 'OK'.


    @Test
    public void registerExistingEmailAndExistingRegisteredGene() throws Exception {

        String url = "/contacts?email=user1@ebi.ac.uk&gene=MGI:0000020&type=gene";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.comparesEqualTo("Register contact user1@ebi.ac.uk for gene MGI:0000020: contact is already registered for that gene.")))
        ;
    }

    @Test
    public void registerNonexistingEmailAndExistingGene() throws Exception {

        String url = "/contacts?email=newuser2@ebi.ac.uk&gene=MGI:0000020&type=gene";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.comparesEqualTo("Register contact newuser2@ebi.ac.uk for gene MGI:0000020: OK")))
        ;

        // Clean up. Remove this registration.
        this.mockMvc.perform(delete(url));
    }

    @Test
    public void registerExistingEmailAndExistingUnregisteredGene() throws Exception {

        String url = "/contacts?email=user3@ebi.ac.uk&gene=MGI:0000020&type=gene";

        this.mockMvc.perform(
                post(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.comparesEqualTo("Register contact user3@ebi.ac.uk for gene MGI:0000020: OK")))
        ;


        // Clean up. Remove this registration.
        this.mockMvc.perform(delete(url));
    }


    // UNREGISTER (DELETE)


    @Test
    public void unregisterOmitType() throws Exception {

        String url = "/contacts?email=user1@ebi.ac.uk&gene=MGI:0000010";

        this.mockMvc.perform(
                delete(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError())
                .andExpect(status().reason("Required String parameter 'type' is not present"))
         ;
    }

    @Test
    public void unregisterMalformedEmailAndGene() throws Exception {

        String url = "/contacts?email=junk&gene=MGI:0000060&type=gene";

        this.mockMvc.perform(
                delete(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", Matchers.comparesEqualTo("Unregister contact junk for gene MGI:0000060 failed: no such active registration exists")))
        ;
    }

    @Test
    public void unregisterExistingEmailAndNonexistingGene() throws Exception {

        String url = "/contacts?email=user1@ebi.ac.uk&gene=junk&type=gene";

        this.mockMvc.perform(
                delete(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", Matchers.comparesEqualTo("Unregister contact user1@ebi.ac.uk for gene junk failed: no such active registration exists")))
        ;
    }

    @Test
    public void unregisterNonexistingEmailAndExistingGene() throws Exception {

        String url = "/contacts?email=junk@ebi.ac.uk&gene=MGI:0000020&type=gene";

        this.mockMvc.perform(
                delete(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", Matchers.comparesEqualTo("Unregister contact junk@ebi.ac.uk for gene MGI:0000020 failed: no such active registration exists")))
        ;
    }

    @Test
    public void unregisterExistingEmailAndExistingUnregisteredGene() throws Exception {

        String url = "/contacts?email=user1@ebi.ac.uk&gene=MGI:0000060&type=gene";

        this.mockMvc.perform(
                delete(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", Matchers.comparesEqualTo("Unregister contact user1@ebi.ac.uk for gene MGI:0000060 failed: no such active registration exists")))
        ;
    }

    @Test
    public void unregisterExistingEmailAndExistingRegisteredGene() throws Exception {

        String url = "/contacts?email=user4@ebi.ac.uk&gene=MGI:0000020&type=gene";

        // First, add a new user and existing gene so we have something valid to unregister. Use new email as existing emails cause other tests to fail since database is not reloaded between tests.
        this.mockMvc.perform(
                post(url)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.comparesEqualTo("Register contact user4@ebi.ac.uk for gene MGI:0000020: OK")))
        ;

        url = "/contacts?email=user4@ebi.ac.uk&gene=MGI:0000020&type=gene";

        this.mockMvc.perform(
                delete(url)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.comparesEqualTo("Unregister contact scheduled for user4@ebi.ac.uk for gene MGI:0000020: OK")))
        ;

    }
}