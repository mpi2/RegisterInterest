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

package org.mousephenotype.ri.integrationtest;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.integrationtest.config.TestConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;

/**
 * Created by mrelac on 16-02-2018.
 */
@RunWith(SpringRunner.class)
//@WebAppConfiguration
@ContextConfiguration(classes = TestConfig.class)
public class SendSummaryTest {

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

    @NotNull
    @Value("${ws-username}")
    String wsUsername;

    @NotNull
    @Value("${ws-password}")
    String wsPassword;


//    @Autowired
//    private ApplicationGenerateSummary applicationGenerateSummary;
//
//    @Autowired
//    private ApplicationSend applicationSend;
//
//    @Autowired
//    private ApplicationContext context;
//
//    @Autowired
//    private InterestController interestController;
//
//    @Autowired
//    private DataSource riDataSource;


    /**
     * This is an end-to-end integration test creates, loads, and uses an embedded in-memory h2 database and a local
     * ri-ws web service instance. It tests the following use case:
     *     register interest in five genes
     *     generateSummary
     *     send
     *
     * Expected results: one e-mail to mrelac@ebi.ac.uk with a summary of the five registered genes
     *
     * @throws Exception
     */
//@Ignore
    @Test
    public void testSendSummary() throws Exception {

//        String   contact          = "mrelac@ebi.ac.uk";
//        String[] geneAccessionIds = new String[] {
//                "MGI:103576",
//                "MGI:1919199",
//                "MGI:2443658",
//                "MGI:2444824",
//                "MGI:3576659"
//        };
//
//        // Load the data
//        Resource r = context.getResource("classpath:sql/h2/schema.sql");
//        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);
//        r = context.getResource("classpath:sql/h2/sendSummary-data.sql");
//        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);
//
//        for (String geneAccessionId : geneAccessionIds) {
//            register(contact, geneAccessionId);
//        }
//        String[] args = new String[0];
//        applicationGenerateSummary.run(args);
//        applicationSend.run(args);
    }


    // PRIVATE METHODS


//    private void register(String email, String geneAccessionId) {
//
//        ResponseEntity<String> response = interestController.register(email, "gene", geneAccessionId);
//        System.out.println(response.getStatusCode().toString());
//        System.out.println(response.getBody());
//    }
}