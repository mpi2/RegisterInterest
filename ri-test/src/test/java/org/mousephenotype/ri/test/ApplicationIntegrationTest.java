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

//    @Ignore
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
            Assert.assertEquals("SUBJECT: " + candidates[i].getSubject(), subjects[i], candidates[i].getSubject());
            Assert.assertEquals("BODY: " + candidates[i].getSubject(), bodies[i], candidates[i].getBody());
        }
    }

    private final String[] subjects = new String[] {
            GENE_010_SUBJECT,
            GENE_030_SUBJECT,
            GENE_040_SUBJECT,
            GENE_220_SUBJECT,
            GENE_050_SUBJECT,
            GENE_060_SUBJECT,
            GENE_070_SUBJECT,
            GENE_080_SUBJECT,
            GENE_090_SUBJECT,
            GENE_100_SUBJECT,
            GENE_110_SUBJECT,
            GENE_120_SUBJECT,
            GENE_130_SUBJECT,
            GENE_140_SUBJECT,
            GENE_150_SUBJECT,
            GENE_160_SUBJECT,
            GENE_170_SUBJECT,
            GENE_180_SUBJECT,
            GENE_190_SUBJECT,
            GENE_200_SUBJECT,
            GENE_210_SUBJECT,
    };

    private final String[] bodies = new String[] {
            GENE_010_BODY,
            GENE_030_BODY,
            GENE_040_BODY,
            GENE_220_BODY,
            GENE_050_BODY,
            GENE_060_BODY,
            GENE_070_BODY,
            GENE_080_BODY,
            GENE_090_BODY,
            GENE_100_BODY,
            GENE_110_BODY,
            GENE_120_BODY,
            GENE_130_BODY,
            GENE_140_BODY,
            GENE_150_BODY,
            GENE_160_BODY,
            GENE_170_BODY,
            GENE_180_BODY,
            GENE_190_BODY,
            GENE_200_BODY,
            GENE_210_BODY
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

    private static final String GENE_030_SUBJECT = "IMPC Gene registration for gene-030";
    private static final String GENE_030_BODY =
            "Dear colleague,\n" +
            "\n" +
            "Thank you for registering interest in gene gene-030.\n" +
            "\n" +
            "This gene has been selected for mouse production and phenotyping as part of the IMPC initiative.\n" +
            "\n" +
            "The IMPC initiative will aim to produce a null allele for this gene, which will enter the IMPC phenotyping pipeline.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_040_SUBJECT = "IMPC Gene registration for gene-040";
    private static final String GENE_040_BODY =
            "Dear colleague,\n" +
            "\n" +
            "Thank you for registering interest in gene gene-040.\n" +
            "\n" +
            "This gene has been selected for mouse production and phenotyping as part of the IMPC initiative.\n" +
            "\n" +
            "Mouse Production for the null allele commenced on 04 July 2017 for this gene.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";
    private static final String GENE_050_SUBJECT = "IMPC Status update for gene-050";
    private static final String GENE_050_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-050 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the null allele commenced on 04 July 2017 and naps-050 produced genotype confirmed mice on 05 July 2017.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_060_SUBJECT = "IMPC Status update for gene-060";
    private static final String GENE_060_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-060 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the null allele commenced on 04 July 2017 and naps-060 produced genotype confirmed mice on 05 July 2017.\n" +
            "\n" +
            "Phenotype data for this gene is now available on the IMPC portal. The IMPC portal is now showing phenotype data and has identified 6 significant phenotypes.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_070_SUBJECT = "IMPC Status update for gene-070";
    private static final String GENE_070_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-070 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the null allele commenced on 04 July 2017 and naps-070 produced genotype confirmed mice on 05 July 2017.\n" +
            "\n" +
            "Additional phenotype data for this gene has become available on the IMPC portal. Phenotype data has been collected and the IMPC portal has identified 7 significant phenotypes.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_080_SUBJECT = "IMPC Status update for gene-080";
    private static final String GENE_080_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-080 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the conditional allele commenced on 02 July 2017 for this gene.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_090_SUBJECT = "IMPC Status update for gene-090";
    private static final String GENE_090_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-090 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the null allele commenced on 04 July 2017 for this gene.\n" +
            "\n" +
            "Mouse Production for the conditional allele commenced on 02 July 2017 for this gene.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_100_SUBJECT = "IMPC Status update for gene-100";
    private static final String GENE_100_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-100 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the null allele commenced on 04 July 2017 and naps-100 produced genotype confirmed mice on 05 July 2017.\n" +
            "\n" +
            "Mouse Production for the conditional allele commenced on 02 July 2017 for this gene.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_110_SUBJECT = "IMPC Status update for gene-110";
    private static final String GENE_110_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-110 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the null allele commenced on 04 July 2017 and naps-110 produced genotype confirmed mice on 05 July 2017.\n" +
            "\n" +
            "Mouse Production for the conditional allele commenced on 02 July 2017 for this gene.\n" +
            "\n" +
            "Phenotype data for this gene is now available on the IMPC portal. The IMPC portal is now showing phenotype data and has identified 11 significant phenotypes.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_120_SUBJECT = "IMPC Status update for gene-120";
    private static final String GENE_120_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-120 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the null allele commenced on 04 July 2017 and naps-120 produced genotype confirmed mice on 05 July 2017.\n" +
            "\n" +
            "Mouse Production for the conditional allele commenced on 02 July 2017 for this gene.\n" +
            "\n" +
            "Additional phenotype data for this gene has become available on the IMPC portal. Phenotype data has been collected and the IMPC portal has identified 12 significant phenotypes.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_130_SUBJECT = "IMPC Status update for gene-130";
    private static final String GENE_130_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-130 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the conditional allele commenced on 02 July 2017 and caps-130 produced genotype confirmed mice on 03 July 2017\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_140_SUBJECT = "IMPC Status update for gene-140";
    private static final String GENE_140_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-140 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the conditional allele commenced on 02 July 2017 and caps-140 produced genotype confirmed mice on 03 July 2017\n" +
            "\n" +
            "Phenotype data for this gene is now available on the IMPC portal. The IMPC portal is now showing phenotype data and has identified 14 significant phenotypes.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_150_SUBJECT = "IMPC Status update for gene-150";
    private static final String GENE_150_BODY =
    "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-150 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the conditional allele commenced on 02 July 2017 and caps-150 produced genotype confirmed mice on 03 July 2017\n" +
            "\n" +
            "Additional phenotype data for this gene has become available on the IMPC portal. Phenotype data has been collected and the IMPC portal has identified 15 significant phenotypes.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_160_SUBJECT = "IMPC Status update for gene-160";
    private static final String GENE_160_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-160 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the null allele commenced on 04 July 2017 for this gene.\n" +
            "\n" +
            "Mouse Production for the conditional allele commenced on 02 July 2017 and caps-160 produced genotype confirmed mice on 03 July 2017\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_170_SUBJECT = "IMPC Status update for gene-170";
    private static final String GENE_170_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-170 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the null allele commenced on 04 July 2017 for this gene.\n" +
            "\n" +
            "Mouse Production for the conditional allele commenced on 02 July 2017 and caps-170 produced genotype confirmed mice on 03 July 2017\n" +
            "\n" +
            "Phenotype data for this gene is now available on the IMPC portal. The IMPC portal is now showing phenotype data and has identified 17 significant phenotypes.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_180_SUBJECT = "IMPC Status update for gene-180";
    private static final String GENE_180_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-180 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the null allele commenced on 04 July 2017 for this gene.\n" +
            "\n" +
            "Mouse Production for the conditional allele commenced on 02 July 2017 and caps-180 produced genotype confirmed mice on 03 July 2017\n" +
            "\n" +
            "Additional phenotype data for this gene has become available on the IMPC portal. Phenotype data has been collected and the IMPC portal has identified 18 significant phenotypes.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_190_SUBJECT = "IMPC Status update for gene-190";
    private static final String GENE_190_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-190 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the null allele commenced on 04 July 2017 and naps-190 produced genotype confirmed mice on 05 July 2017.\n" +
            "\n" +
            "Mouse Production for the conditional allele commenced on 02 July 2017 and caps-190 produced genotype confirmed mice on 03 July 2017\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_200_SUBJECT = "IMPC Status update for gene-200";
    private static final String GENE_200_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-200 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the null allele commenced on 04 July 2017 and naps-200 produced genotype confirmed mice on 05 July 2017.\n" +
            "\n" +
            "Mouse Production for the conditional allele commenced on 02 July 2017 and caps-200 produced genotype confirmed mice on 03 July 2017\n" +
            "\n" +
            "Phenotype data for this gene is now available on the IMPC portal. The IMPC portal is now showing phenotype data and has identified 20 significant phenotypes.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_210_SUBJECT = "IMPC Status update for gene-210";
    private static final String GENE_210_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have registered interest in gene gene-210 via the IMPC (www.mousephenotype.org). You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n" +
            "\n" +
            "Mouse Production for the null allele commenced on 04 July 2017 and naps-210 produced genotype confirmed mice on 05 July 2017.\n" +
            "\n" +
            "Mouse Production for the conditional allele commenced on 02 July 2017 and caps-210 produced genotype confirmed mice on 03 July 2017\n" +
            "\n" +
            "Additional phenotype data for this gene has become available on the IMPC portal. Phenotype data has been collected and the IMPC portal has identified 21 significant phenotypes.\n" +
            "\n" +
            "You will be notified by email with any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";

    private static final String GENE_220_SUBJECT = "IMPC Gene unregistration for gene-220";
    private static final String GENE_220_BODY =
            "Dear colleague,\n" +
            "\n" +
            "You have been unregistered for interest in gene gene-220.\n" +
            "\n" +
            "You will no longer be notified about any future changes in this gene's status.\n" +
            "\n" +
            "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
            "\n" +
            "Best Regards,\n" +
            "\n" +
            "The MPI2 (KOMP2) informatics consortium";


    private class SubjectComparator implements Comparator<GeneSent> {

        @Override
        public int compare(GeneSent o1, GeneSent o2) {
            return o1.getSubject().compareTo(o2.getSubject());
        }
    }
}