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

package org.mousephenotype.ri.generate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.Gene;
import org.mousephenotype.ri.core.entities.GeneContact;
import org.mousephenotype.ri.core.entities.GeneSent;
import org.mousephenotype.ri.generate.config.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

//import org.springframework.core.io.Resource;
//import org.springframework.jdbc.datasource.init.ScriptUtils;


/**
 * Created by mrelac on 21/06/2017.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = TestConfig.class)
public class ApplicationGenerateTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SqlUtils sqlUtils;

    @Autowired
    @Qualifier("riDataSource")
    private DataSource ds;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @Test
    public void testRegisterInterest() throws Exception {
//        Resource r = context.getResource("classpath:sql/h2/generate-data.sql");
//        ScriptUtils.executeSqlScript(ds.getConnection(), r);

        ApplicationGenerate app = new ApplicationGenerate(sqlUtils);
        context.getAutowireCapableBeanFactory().autowireBean(app);
        context.getAutowireCapableBeanFactory().initializeBean(app, "application");
        app.run();

        // Unregister
        Gene unregisterGene = sqlUtils.getGene("MGI:0000120");
        GeneContact unregisterGeneContact = sqlUtils.getGeneContact("MGI:0000120", "user4@ebi.ac.uk", 1);
        app.generateUnregisterGeneEmail(unregisterGene, unregisterGeneContact);

        // Write to csv: Everything but the body
        jdbc.getJdbcOperations().execute("CALL CSVWRITE('target/gene_sent1.csv', 'SELECT pk, gene_contact_pk, assignment_status_pk, conditional_allele_production_status_pk, null_allele_production_status_pk, phenotyping_status_pk, created_at, sent_at, updated_at, subject FROM gene_sent WHERE sent_at IS NULL', 'charset=UTF-8 fieldSeparator=,')");
        //               body
        jdbc.getJdbcOperations().execute("CALL CSVWRITE('target/gene_sent2.csv', 'SELECT body FROM gene_sent WHERE sent_at IS NULL', 'charset=UTF-8 fieldSeparator=,')");

        List<GeneSent> genesSent = new ArrayList(sqlUtils.getGeneSent().values());
        int i = 0;
        for (GeneSent geneSent : genesSent) {
            if (geneSent.getSentAt() != null) {
                continue;
            }

            System.out.println("\n[" + i + "]:");
            System.out.println("SUBJECT: '" + geneSent.getSubject() + "'");
            System.out.println(geneSent.getBody());
            i++;
        }

        System.out.println();
    }
}