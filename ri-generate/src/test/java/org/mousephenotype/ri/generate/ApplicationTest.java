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
import java.sql.Connection;
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
public class ApplicationTest {


    private Connection connection;

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

        Application app = new Application();
        context.getAutowireCapableBeanFactory().autowireBean(app);
        context.getAutowireCapableBeanFactory().initializeBean(app, "application");
        app.run();

        // Unregister
        Gene unregisterGene = sqlUtils.getGene("MGI:0000120");
        GeneContact unregisterGeneContact = sqlUtils.getGeneContact("MGI:0000120", "user4@ebi.ac.uk", 1);
        app.generateUnregisterGeneEmail(unregisterGene, unregisterGeneContact);

        // Write to csv: Everything but the body
        jdbc.getJdbcOperations().execute("CALL CSVWRITE('gene_sent1.csv', 'SELECT pk, gene_contact_pk, assignment_status_pk, conditional_allele_production_status_pk, null_allele_production_status_pk, phenotyping_status_pk, created_at, sent_at, updated_at, subject FROM gene_sent WHERE sent_at IS NULL', 'charset=UTF-8 fieldSeparator=,')");
        //               body
        jdbc.getJdbcOperations().execute("CALL CSVWRITE('gene_sent2.csv', 'SELECT                body FROM gene_sent WHERE sent_at IS NULL', 'charset=UTF-8 fieldSeparator=,')");

        List<GeneSent> genesSent = new ArrayList(sqlUtils.getGenesSent().values());
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