package org.mousephenotype.ri.test;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.Gene;
import org.mousephenotype.ri.core.entities.GeneContact;
import org.mousephenotype.ri.core.entities.GeneSent;
import org.mousephenotype.ri.generate.ApplicationGenerate;
import org.mousephenotype.ri.send.ApplicationSend;
import org.mousephenotype.ri.test.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by mrelac on 21/06/2017.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = AppConfig.class)
public class ApplicationIntegrationTest {

    @Autowired
    private ApplicationContext generateContext;

    @Autowired
    private ApplicationContext sendContext;

    @Autowired
    private SqlUtils sqlutils;

    public final static boolean SEND_EMAIL = false;

    @Ignore
    @Test
    public void testGenerateAndSendRegisterInterest() throws Exception {

        System.out.println();
        ApplicationGenerate generateApp = new ApplicationGenerate(sqlutils);
        generateContext.getAutowireCapableBeanFactory().autowireBean(generateApp);
        generateContext.getAutowireCapableBeanFactory().initializeBean(generateApp, "generateApp");

        Gene gene = sqlutils.getGene("MGI:0000220");
        GeneContact geneContact = sqlutils.getGeneContact("MGI:0000220", "mrelac@ebi.ac.uk", 1);

        generateApp.run();
        generateApp.generateUnregisterGeneEmail(gene, geneContact);
        checkSent(generateApp);

        if (SEND_EMAIL) {
            ApplicationSend sendApp = new ApplicationSend(sqlutils);
            sendContext.getAutowireCapableBeanFactory().autowireBean(sendApp);
            sendContext.getAutowireCapableBeanFactory().initializeBean(sendApp, "sendApp");

            sendApp.run();
        }
    }


    // PRIVATE METHODS


    private void checkSent(ApplicationGenerate generateApp) {

        ArrayList<GeneSent> candidateList = new ArrayList<>(sqlutils.getGenesScheduledForSending());
        GeneSent[] candidates = candidateList.toArray(new GeneSent[0]);

        // We expect 21 emails.
        Assert.assertEquals("Expected 21 results", candidates.length, 21);

        // Sort the GeneSent objects by subject.
        Arrays.sort(candidates, new SubjectComparator());

        for (int i = 0; i < candidates.length; i++) {
            Assert.assertEquals(candidates[i].getSubject(), subjects[i]);
            Assert.assertEquals(candidates[i].getBody(), bodies[i]);
        }
    }

    private final String[] subjects = new String[] {
            GENE_010_SUBJECT
    };

    private final String[] bodies = new String[] {
            GENE_010_BODY
    };
    private static final String GENE_010_SUBJECT = "IMPC Gene registration for gene-010";
    private static final String GENE_010_BODY =
    "Dear colleague,\n"+
            "\n"+
            "Thank you for registering interest in gene gene-010.\n"+
            "\n"+
            "This gene has not been selected for mouse production and phenotyping as part of the IMPC initiative. This gene will be considered for mouse production in the future by the IMPC.\n"+
            "\n"+
            "You will be notified by email with any future changes in this gene's status.\n"+
            "\n"+
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n"+
            "\n"+
            "Best Regards,\n"+
            "\n"+
            "The MPI2 (KOMP2) informatics consortium";

    private class SubjectComparator implements Comparator<GeneSent> {

        @Override
        public int compare(GeneSent o1, GeneSent o2) {
            return o1.getSubject().compareTo(o2.getSubject());
        }
    }
}