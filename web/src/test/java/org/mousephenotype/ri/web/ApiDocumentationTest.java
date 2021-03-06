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

package org.mousephenotype.ri.web;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Mike Relac
 * @date 26/06/2017
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
public class ApiDocumentationTest extends BaseTest {

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

        // Good api testing docs: https://docs.spring.io/spring-restdocs/docs/current/reference/html5/#configuration and
        //                        https://asciidoctor.org/docs/asciidoc-writers-guide
        // Alternate:
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
//                .apply(documentationConfiguration(this.restDocumentation).uris()
//                .withScheme("https")
//                       .withHost("mousephenotype.org")
//                       .withPort(443)
//                )
//                .alwaysDo(this.restDocumentationResultHandler)
//                .build()
//        ;



    }

    FieldDescriptor[] summary = new FieldDescriptor[] {
            fieldWithPath("emailAddress").description("The contact's e-mail address"),
            fieldWithPath("genes").description("The complete list of genes for which the contact has registered interest")
    };

    FieldDescriptor[] gene = new FieldDescriptor[] {
            fieldWithPath("pk").description("The primary key of the gene instance"),
            fieldWithPath("mgiAccessionId").description("The gene's MGI gene accession id"),
            fieldWithPath("symbol").description("The gene's symbol"),
            fieldWithPath("riAssignmentStatus").description("The gene's assignment status"),
            fieldWithPath("riConditionalAlleleProductionStatus").description("The gene's conditional allele production status"),
            fieldWithPath("riNullAlleleProductionStatus").description("The gene's null allele production status"),
            fieldWithPath("riPhenotypingStatus").description("The gene's phenotyping status"),
            fieldWithPath("createdAt").description("The time and date the gene was created"),
            fieldWithPath("updatedAt").description("The time and date the gene was last updated")
    };

    FieldDescriptor [] geneAccessionIds = new FieldDescriptor[] {
            fieldWithPath("geneAccessionIds").description("The MGI gene accession ids of the genes for which the contact has registered interest")
    };

    @Test
    @WithMockUser("user1@ebi.ac.uk")
    public void summaryGet() {

        this.restDocumentationResultHandler.document(
                requestParameters(),
                responseFields(
                        summary
                ))
        ;
    }

    @Test
    @WithMockUser("user1@ebi.ac.uk")
    public void summaryGetList() {

        this.restDocumentationResultHandler.document(
                requestParameters(),
                responseFields(
                        geneAccessionIds
                ))
        ;
    }

    @Test
    @WithMockUser("user1@ebi.ac.uk")
    public void register() throws Exception {
        this.mockMvc.perform(post("/mi/impc/interest/api/registration/gene?geneAccessionId=MGI:2444824")
                .contextPath("/mi/impc/interest")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("register",
                    requestParameters(
                            parameterWithName("geneAccessionId").description("The gene's MGI gene accession id")
                    )))
        ;
    }

    @Test
    @WithMockUser("user1@ebi.ac.uk")
    public void unregister() throws Exception {
        this.mockMvc.perform(delete("/mi/impc/interest/api/unregistration/gene?geneAccessionId=MGI:103576")
            .contextPath("/mi/impc/interest")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("unregister",
                requestParameters(
                    parameterWithName("geneAccessionId").description("The gene's MGI gene accession id")
            )))
        ;
    }
}