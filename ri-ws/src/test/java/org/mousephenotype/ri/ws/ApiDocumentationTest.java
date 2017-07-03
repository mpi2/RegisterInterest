package org.mousephenotype.ri.ws;

import org.junit.Before;
import org.junit.Ignore;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
            fieldWithPath("active").description("A flag indicating whether or not the contact is active (1) or inactive (0)")
    };

    FieldDescriptor[] gene = new FieldDescriptor[] {
            fieldWithPath("pk").description("The primary key of the gene instance"),
            fieldWithPath("mgiAccessionId").description("The gene's MGI gene accession id"),
            fieldWithPath("symbol").description("The gene's symbol"),
            fieldWithPath("createdAt").description("The time and date the gene was created"),
            fieldWithPath("updatedAt").description("The time and date the gene was last updated")
    };

    @Test
    public void filteredContactGet() throws Exception {
        this.mockMvc.perform(get("/mi/impc/dev/interest/contacts?type=gene&email=user1@ebi.ac.uk&gene=MGI:0000010")
                .contextPath("/mi/impc/dev/interest")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(this.restDocumentationResultHandler.document(
                        pathParameters(
                                parameterWithName("type").description("The type of register interest (e.g. gene, disease, phenotype) to further filter by").optional(),
                                parameterWithName("gene").description("The MGI gene accession id to further filter by").optional(),
                                parameterWithName("email").description("The email address to further filter by").optional()),

                        responseFields(
                                fieldWithPath("[*].contact.pk").description("The primary key of the contact instance"),
                                fieldWithPath("[*].contact.address").description("The contact's email address"),
                                fieldWithPath("[*].contact.createdAt").description("The time and date the contact was created"),
                                fieldWithPath("[*].contact.updatedAt").description("The time and date the contact was last updated"),
                                fieldWithPath("[*].contact.active").description("A flag indicating whether or not the contact is active (1) or inactive (0)"),

                                fieldWithPath("[*].genes[*].pk").description("The primary key of the gene instance"),
                                fieldWithPath("[*].genes[*].mgiAccessionId").description("The gene's MGI gene accession id"),
                                fieldWithPath("[*].genes[*].symbol").description("The gene's symbol"),
                                fieldWithPath("[*].genes[*].createdAt").description("The time and date the gene was created"),
                                fieldWithPath("[*].genes[*].updatedAt").description("The time and date the gene was last updated")
                        )))
                ;
    }

    @Test
    public void register() throws Exception {
        this.mockMvc.perform(post("/mi/impc/dev/interest/contacts?type=gene&email=user10@ebi.ac.uk&gene=MGI:0000010")
                .contextPath("/mi/impc/dev/interest")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("register",
                        requestParameters(
                                parameterWithName("type").description("The type of register interest (e.g. gene, disease, phenotype) to further filter by"),
                                parameterWithName("gene").description("The MGI gene accession id to further filter by"),
                                parameterWithName("email").description("The email address to further filter by"))))
        ;
    }

    @Test
    public void unregister() throws Exception {
        this.mockMvc.perform(delete("/mi/impc/dev/interest/contacts?type=gene&email=user10@ebi.ac.uk&gene=MGI:0000010")
                .contextPath("/mi/impc/dev/interest")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("unregister",
                        requestParameters(
                                parameterWithName("type").description("The type of unregister interest (e.g. gene, disease, phenotype) to further filter by"),
                                parameterWithName("gene").description("The MGI gene accession id to further filter by"),
                                parameterWithName("email").description("The email address to further filter by"))))
        ;
    }
}