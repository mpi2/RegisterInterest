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


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.GeneSent;
import org.mousephenotype.ri.core.entities.GeneSentSummary;
import org.mousephenotype.ri.generate.ApplicationGenerate;
import org.mousephenotype.ri.generate.ApplicationGenerateSummary;
import org.mousephenotype.ri.integrationtest.config.TestConfig;
import org.mousephenotype.ri.send.ApplicationSend;
import org.mousephenotype.ri.web.controller.InterestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by mrelac on 16-02-2018.
 */
@RunWith(SpringRunner.class)
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


    @Autowired
    private ApplicationGenerate applicationGenerate;

    @Autowired
    private ApplicationGenerateSummary applicationGenerateSummary;

    @Autowired
    private ApplicationSend applicationSend;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private InterestController interestController;

    @Autowired
    private DataSource riDataSource;

    @Autowired
    private SqlUtils sqlUtils;


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
@Ignore
    @Test
    public void testSendSummary() throws Exception {

        String   contact          = "mrelac@ebi.ac.uk";
        String[] geneAccessionIds = new String[] {
                "MGI:103576",
                "MGI:1919199",
                "MGI:2443658",
                "MGI:2444824",
                "MGI:3576659"
        };

        // Load the data
        Resource r = context.getResource("classpath:sql/h2/schema.sql");
        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);
        r = context.getResource("classpath:sql/h2/sendSummary-data.sql");
        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);

        for (String geneAccessionId : geneAccessionIds) {
            register(contact, geneAccessionId);
        }
        String[] args = new String[0];

        applicationGenerate.run(args);

        applicationGenerateSummary.run(args);

        int pendingCount = sqlUtils.getGeneSentSummaryPendingEmailCount();
        Assert.assertTrue("Expected at least one pending summary count", pendingCount > 0);                                 // There should be at least one pending summary count.

        List<GeneSent> genesScheduledForSending = sqlUtils.getGenesScheduledForSending();
        pendingCount = genesScheduledForSending.size();
        Assert.assertTrue("Expected at least one gene scheduled for sending but found " + pendingCount, pendingCount > 0);  // There should be at least one gene scheduled for sending.

        // NOTE: Remember, applicationSend currently has a 36-second pause built in to avoid being marked as spam.
        applicationSend.run(args);

        pendingCount = sqlUtils.getGeneSentSummaryPendingEmailCount();
        Assert.assertEquals("Expected no pending summary counts but found " + pendingCount, 0, pendingCount);               // Summary pending count should be reset to 0 by applicationSend.

        genesScheduledForSending = sqlUtils.getGenesScheduledForSending();
        pendingCount = genesScheduledForSending.size();
        Assert.assertEquals("Expected no genes scheduled for sending but found " + pendingCount, 0, pendingCount);          // The genes scheduled for sending count should be reset to 0 by applicationSend.

        // Assert that the gene_sent dates match the summary sent_at date.
        GeneSentSummary summary = sqlUtils.getGeneSentSummaryForContact(contact);
        Assert.assertNotNull("Expected GeneSentSummary value but was null", summary);

        List<GeneSent> genesSent = sqlUtils.getGeneSentForContact(contact);
        Assert.assertTrue(genesSent.size() > 0);

        for (GeneSent geneSent : genesSent) {
            Assert.assertEquals(summary.getSentAt(), (geneSent.getSentAt()));
        }
    }


    // PRIVATE METHODS


    private void register(String email, String geneAccessionId) {

        ResponseEntity<String> response = interestController.doGeneRegistration(geneAccessionId);
        System.out.println(response.getStatusCode().toString());
        System.out.println(response.getBody());
    }
}