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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.ws.config.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Mike Relac
 * @date 26/06/2017
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
public class ApiDocumentationTest {

    @Rule
    public final JUnitRestDocumentation
            restDocumentation = new JUnitRestDocumentation("src/main/asciidoc/generated-snippets");

    private RestDocumentationResultHandler restDocumentationResultHandler;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {

        this.restDocumentationResultHandler = document("{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
        );

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(this.restDocumentationResultHandler)
                .build()
        ;
    }

    FieldDescriptor[] contact = new FieldDescriptor[] {
            fieldWithPath("pk").description("The primary key of the contact instance"),
            fieldWithPath("address").description("The contact's email address"),
            fieldWithPath("createdAt").description("The time and date the contact was created"),
            fieldWithPath("updatedAt").description("The time and date the contact was last updated"),
            fieldWithPath("active").description("A flag indicating whether or not the contact is active (true) or inactive (false)")
    };

    FieldDescriptor[] gene = new FieldDescriptor[] {
            fieldWithPath("pk").description("The primary key of the gene instance"),
            fieldWithPath("mgiAccessionId").description("The gene's MGI gene accession id"),
            fieldWithPath("symbol").description("The gene's symbol"),
            fieldWithPath("createdAt").description("The time and date the gene was created"),
            fieldWithPath("updatedAt").description("The time and date the gene was last updated")
    };

    // FIXME FIXME FIXME
    // Replace this test (and the /interest/contacts endpoint) with a test for the Summary object.
    // FIXME FIXME FIXME
    @Test
    public void filteredContactGet() throws Exception {
        this.mockMvc.perform(get("https://www.ebi.ac.uk/mi/impc/interest/contacts?type=gene&email=user1@ebi.ac.uk&gene=MGI:0000010")
                .contextPath("/mi/impc/interest")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.restDocumentationResultHandler.document(
                        requestParameters(
                                parameterWithName("type").description("The type of register interest (e.g. gene, disease, phenotype) to further filter by (optional)").optional(),
                                parameterWithName("gene").description("The MGI gene accession id to further filter by (optional)").optional(),
                                parameterWithName("email").description("The email address to further filter by (optional)").optional()),

                        responseFields(
                                fieldWithPath("[*].contact.pk").description("The primary key of the contact instance"),
                                fieldWithPath("[*].contact.emailAddress").description("The contact's email address"),
                                fieldWithPath("[*].contact.password").description("The contact's password"),
                                fieldWithPath("[*].contact.roles").description("A list of the contact's roles"),
                                fieldWithPath("[*].contact.createdAt").description("The time and date the contact was created"),
                                fieldWithPath("[*].contact.updatedAt").description("The time and date the contact was last updated"),
                                fieldWithPath("[*].contact.accountLocked").description("A flag indicating whether or not the account is locked"),
                                fieldWithPath("[*].contact.passwordExpired").description("A flag indicating whether or not the password has expired"),

                                fieldWithPath("[*].genes[*].pk").description("The primary key of the gene instance"),
                                fieldWithPath("[*].genes[*].mgiAccessionId").description("The gene's MGI gene accession id"),
                                fieldWithPath("[*].genes[*].symbol").description("The gene's symbol"),
                                fieldWithPath("[*].genes[*].riAssignmentStatus").description("The gene's register interest Assignment status"),
                                fieldWithPath("[*].genes[*].riConditionalAlleleProductionStatus").description("The gene's register interest Conditional Allele Production status"),
                                fieldWithPath("[*].genes[*].riNullAlleleProductionStatus").description("The gene's register interest Null Allele Production status"),
                                fieldWithPath("[*].genes[*].riPhenotypingStatus").description("The gene's register interest Phenotyping status"),
                                fieldWithPath("[*].genes[*].createdAt").description("The time and date the gene was created"),
                                fieldWithPath("[*].genes[*].updatedAt").description("The time and date the gene was last updated")
                        )))
                ;
    }

    @Test
    public void register() throws Exception {
        this.mockMvc.perform(post("https://www.ebi.ac.uk/mi/impc/interest/contacts?type=gene&email=user10@ebi.ac.uk&gene=MGI:0000010")
                .contextPath("/mi/impc/interest")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("register",
                        requestParameters(
                                parameterWithName("type").description("The type of interest (e.g. gene, disease, phenotype) to register (required)"),
                                parameterWithName("gene").description("The MGI gene accession id to register (required)"),
                                parameterWithName("email").description("The email address to register (required)"))))
        ;
    }

    @Test
    public void unregister() throws Exception {
        this.mockMvc.perform(delete("https://www.ebi.ac.uk/mi/impc/interest/contacts?type=gene&email=user10@ebi.ac.uk&gene=MGI:0000010")
                .contextPath("/mi/impc/interest")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("unregister",
                        requestParameters(
                                parameterWithName("type").description("The type of interest (e.g. gene, disease, phenotype) to unregister (required)"),
                                parameterWithName("gene").description("The MGI gene accession id to unregister (required)"),
                                parameterWithName("email").description("The email address to unregister (required)"))))
        ;
    }
}