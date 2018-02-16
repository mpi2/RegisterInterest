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

import org.h2.tools.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.GeneSent;
import org.mousephenotype.ri.core.entities.GeneSentSummary;
import org.mousephenotype.ri.generate.config.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private DataSource riDataSource;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;



    // Set startServer to true to produce an in-memory h2 database browser.
    private static boolean startServer = false;
    private static Server server;

    private Thread thread;
    @Before
    public void before() throws SQLException {


        // Show browser if startServer is true.
        if (startServer) {
            startServer = false;
            Runnable runnable = () -> {

                try {
                    Server.startWebServer(riDataSource.getConnection());

                    server = Server.createWebServer("-web");  // .start();
                    server.start();
                    System.out.println("URL: " + server.getURL());
                    System.out.println("Port: " + server.getPort());
                    Server.openBrowser(server.getURL());

                } catch (Exception e) {
                    System.out.println("Embedded h2 server failed to start: " + e.getLocalizedMessage());
                    System.exit(1);
                }
            };

            thread = new Thread(runnable);
            thread.start();
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            }
        }


        // Reload database.
        Resource r;
        r = context.getResource("classpath:sql/h2/schema.sql");
        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);
    }


    // NOTE: This is not really a test as much as it is some code to generate email content and look at the results in a csv generated by the in-memory database.
    @Test
//@Ignore
    public void testGenerate() throws Exception {

        Resource r = context.getResource("classpath:sql/h2/generate-data.sql");
        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);

        ApplicationGenerate applicationGenerate = new ApplicationGenerate(sqlUtils);
        context.getAutowireCapableBeanFactory().autowireBean(applicationGenerate);
        context.getAutowireCapableBeanFactory().initializeBean(applicationGenerate, "application");

        applicationGenerate.run();

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

    // NOTE: This is not really a test as much as it is some code to generate email content and look at the results in a csv generated by the in-memory database.
//@Ignore
    @Test
    public void testGenerateSummary() throws Exception {

        Resource r = context.getResource("classpath:sql/h2/generate-summary-data.sql");
        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);

        ApplicationGenerateSummary applicationGenerateSummary = new ApplicationGenerateSummary(sqlUtils);
        context.getAutowireCapableBeanFactory().autowireBean(applicationGenerateSummary);
        context.getAutowireCapableBeanFactory().initializeBean(applicationGenerateSummary, "application");

        applicationGenerateSummary.run();

        // Write to csv: Everything but the body
        jdbc.getJdbcOperations().execute("CALL CSVWRITE('target/gene_sent_summary1.csv', 'SELECT pk, contact_pk, created_at, sent_at, updated_at, subject FROM gene_sent_summary WHERE sent_at IS NULL', 'charset=UTF-8 fieldSeparator=,')");
        //               body
        jdbc.getJdbcOperations().execute("CALL CSVWRITE('target/gene_sent_summary2.csv', 'SELECT body FROM gene_sent_summary WHERE sent_at IS NULL', 'charset=UTF-8 fieldSeparator=,')");

        Map<Integer, GeneSentSummary> summaryMap = sqlUtils.getGeneSentSummary();
        Assert.assertEquals(1, summaryMap.size());
        GeneSentSummary summary = summaryMap.values().iterator().next();
        Assert.assertNull(summary.getSentAt());

//        System.out.println("SUBJECT: '" + summary.getSubject() + "'");
//        System.out.println(summary.getBody());


        final String expectedSubject = "Complete list of IMPC genes for which you have registered interest";
        final String expectedBody = "<html>Dear colleague,<br /><br />Below please find a summary of the IMPC genes for which you have registered interest.<br /><br /><style>  table {    font-family: arial, sans-serif;    border-collapse: collapse;    width: 100%;}td, th {    border: 1px solid #dddddd;    text-align: left;    padding: 8px;}tr:nth-child(even) {    background-color: #dddddd;}</style><table id=\"genesTable\"><tr><th>Gene Symbol</th><th>Gene MGI Accession Id</th><th>Assignment Status</th><th>Null Allele Production</th><th>Conditional Allele Production</th><th>Phenotyping Data Available</th><th>Action</th></tr><tr><td><a href=\"http://www.mousephenotype.org/data/genes/MGI:3576659\" alt =\"http://www.mousephenotype.org/data/genes/MGI:3576659\">Ano5</a></td><td><a href=\"http://www.informatics.jax.org/marker/MGI:3576659\" alt =\"http://www.informatics.jax.org/marker/MGI:3576659\">MGI:3576659</a></td><td>Selected for production and phenotyping</td><td>Started</td><td>Started</td><td>No</td><td><a href=\"http://www.mousephenotype.org/data/search/gene?kw=&quot;MGI:3576659&quot;\" alt =\"http://www.mousephenotype.org/data/search/gene?kw=&quot;MGI:3576659&quot;\">Unregister</a></td></tr><tr><td><a href=\"http://www.mousephenotype.org/data/genes/MGI:103576\" alt =\"http://www.mousephenotype.org/data/genes/MGI:103576\">Ccl11</a></td><td><a href=\"http://www.informatics.jax.org/marker/MGI:103576\" alt =\"http://www.informatics.jax.org/marker/MGI:103576\">MGI:103576</a></td><td>Withdrawn</td><td>None</td><td>None</td><td>No</td><td><a href=\"http://www.mousephenotype.org/data/search/gene?kw=&quot;MGI:103576&quot;\" alt =\"http://www.mousephenotype.org/data/search/gene?kw=&quot;MGI:103576&quot;\">Unregister</a></td></tr><tr><td><a href=\"http://www.mousephenotype.org/data/genes/MGI:1919199\" alt =\"http://www.mousephenotype.org/data/genes/MGI:1919199\">Cers5</a></td><td><a href=\"http://www.informatics.jax.org/marker/MGI:1919199\" alt =\"http://www.informatics.jax.org/marker/MGI:1919199\">MGI:1919199</a></td><td>Selected for production and phenotyping</td><td><a href=\"http://www.mousephenotype.org/data/search/allele2?kw=&quot;MGI:1919199&quot;\" alt =\"http://www.mousephenotype.org/data/search/allele2?kw=&quot;MGI:1919199&quot;\">Genotype confirmed mice</a></td><td><a href=\"http://www.mousephenotype.org/data/search/allele2?kw=&quot;MGI:1919199&quot;\" alt =\"http://www.mousephenotype.org/data/search/allele2?kw=&quot;MGI:1919199&quot;\">Genotype confirmed mice</a></td><td><a href=\"http://www.mousephenotype.org/data/genes/MGI:1097680#section-associations\" alt =\"http://www.mousephenotype.org/data/genes/MGI:1097680#section-associations\">Yes</a></td><td><a href=\"http://www.mousephenotype.org/data/search/gene?kw=&quot;MGI:1919199&quot;\" alt =\"http://www.mousephenotype.org/data/search/gene?kw=&quot;MGI:1919199&quot;\">Unregister</a></td></tr><tr><td><a href=\"http://www.mousephenotype.org/data/genes/MGI:2443658\" alt =\"http://www.mousephenotype.org/data/genes/MGI:2443658\">Prr14l</a></td><td><a href=\"http://www.informatics.jax.org/marker/MGI:2443658\" alt =\"http://www.informatics.jax.org/marker/MGI:2443658\">MGI:2443658</a></td><td>Selected for production and phenotyping</td><td>Started</td><td>None</td><td>No</td><td><a href=\"http://www.mousephenotype.org/data/search/gene?kw=&quot;MGI:2443658&quot;\" alt =\"http://www.mousephenotype.org/data/search/gene?kw=&quot;MGI:2443658&quot;\">Unregister</a></td></tr><tr><td><a href=\"http://www.mousephenotype.org/data/genes/MGI:2444824\" alt =\"http://www.mousephenotype.org/data/genes/MGI:2444824\">Sirpb1a</a></td><td><a href=\"http://www.informatics.jax.org/marker/MGI:2444824\" alt =\"http://www.informatics.jax.org/marker/MGI:2444824\">MGI:2444824</a></td><td>Not planned</td><td><a href=\"http://www.mousephenotype.org/data/search/allele2?kw=&quot;MGI:2444824&quot;\" alt =\"http://www.mousephenotype.org/data/search/allele2?kw=&quot;MGI:2444824&quot;\">Genotype confirmed mice</a></td><td>None</td><td>No</td><td><a href=\"http://www.mousephenotype.org/data/search/gene?kw=&quot;MGI:2444824&quot;\" alt =\"http://www.mousephenotype.org/data/search/gene?kw=&quot;MGI:2444824&quot;\">Unregister</a></td></tr></table><br />For further information / enquiries please write to:<a href=\"mailto:mouse-helpdesk@ebi.ac.uk\">mouse-helpdesk@ebi.ac.uk</a>.<br /><br />Best Regards,<br /><br />The MPI2 (KOMP2) informatics consortium</html>";

        Assert.assertEquals(expectedSubject, summary.getSubject());
        Assert.assertEquals(expectedBody, summary.getBody());

    }
}