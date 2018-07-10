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


import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.generate.ApplicationGenerate;
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

/**
 * Created by mrelac on 21/06/2017.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class RegisterUnregisterTest {

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
    private ApplicationSend applicationSend;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private InterestController interestController;

    @Autowired
    private DataSource riDataSource;


    /**
     * This is an end-to-end integration test creates, loads, and uses an embedded in-memory h2 database and a local
     * ri-ws web service instance. It tests the following use case:
     *     register interest
     *     unregister interest
     *     generate
     *     send
     *
     * Expected results: four e-mails, two to mrelac@ebi.ac.uk and two to mike@foxhill.com (each address gets
     * two e-mails: one for MGI:1919199 and one for MGI:102851)
     *
     * @throws Exception
     */
@Ignore
    @Test
    public void testRegisterUnregister() throws Exception {

        String[] contacts         = new String[]{"mrelac@ebi.ac.uk", "mike@foxhill.com"};
        String[] geneAccessionIds = new String[]{"MGI:1919199", "MGI:102851"};

        // Load the data
        Resource r = context.getResource("classpath:sql/h2/schema.sql");
        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);
        r = context.getResource("classpath:sql/h2/registerUnregister-data.sql");
        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);

        for (int i = 0; i < contacts.length; i++) {
            register(contacts[i], geneAccessionIds[i]);
            unregister(contacts[i], geneAccessionIds[i]);
        }
        String[] args = new String[0];
        applicationGenerate.run(args);
        applicationSend.run(args);
    }


    // Web Service support routines


    private void register(String email, String geneAccessionId) {

        ResponseEntity<String> response = interestController.apiRegistrationGene(geneAccessionId);
        System.out.println(response.getStatusCode().toString());
        System.out.println(response.getBody());
    }

    private void unregister(String email, String geneAccessionId) {

        ResponseEntity<String> response = interestController.apiUnregistrationGene(geneAccessionId);
        System.out.println(response.getStatusCode().toString());
        System.out.println(response.getBody());
    }
}