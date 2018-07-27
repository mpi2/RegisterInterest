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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.ri.core.utils.SqlUtils;
import org.mousephenotype.ri.core.entities.ContactGene;
import org.mousephenotype.ri.core.entities.Gene;
import org.mousephenotype.ri.core.entities.GeneSent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by mrelac on 05/06/2017.
 *
 * This class is intended to be a command-line callable java main program that generates e-mails to contacts registered
 * for insterest in specific genes whose status indicates the gene state has changed.
 */
// FIXME
@SpringBootApplication
@Deprecated
public class ApplicationGenerate implements CommandLineRunner {

    private final       Logger  logger      = LoggerFactory.getLogger(this.getClass());
    public static final String  WELCOME_ARG = "welcome";
    public static final String  SUMMARY_ARG = "summary";
    private             boolean createWelcomeText;
    private             boolean createSummaryText;
    private             String  createSummaryTextFor;









    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM yyyy");

    private SqlUtils sqlUtils;

    public final Set<String> errorMessages = ConcurrentHashMap.newKeySet();
    private List<ContactGene> contactGenes;
    private Map<Integer, Gene> genesMap;
    private Map<Integer, GeneSent> geneSentMap;


    @Inject
    public ApplicationGenerate(SqlUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ApplicationGenerate.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }


    /**
     * Additional supported arguments:
     *   -- welcome emailAddress        # Generates and sends a single welcome e-mail to the specified email address
     *   -- summary emailAddress        # Generates and sends a single summary e-mail to the specified email address
     *   -- summary                     # Generates and sends a summary to each registered user
     */
    @Override
    public void run(String... args) throws Exception {

        OptionSet options = parseOptions(args);

        logger.info("Program Arguments: " + StringUtils.join(args, ", "));

        if (createWelcomeText) {
            logger.info("createWelcomeText!");
        }
        if (createSummaryText) {
            logger.info("createSummaryText for {}", createSummaryTextFor == null ? "Everyone" : createSummaryTextFor);
        }
//
//        int count = 0;
//        String message;
//
//        contactGenes = sqlUtils.getContactGenes();
//        genesMap = sqlUtils.getGenesByPk();
//        geneSentMap = sqlUtils.getGeneSent();
//
//        /**
//         * For each contactGene:
//         *      - Look up the Gene object from genesMap using the gene primary key (found in the contactGene object)
//         *      - Get the GeneSent object from the geneSentMap using the contactGene primary key.
//         *      - If it is not found
//         *          - create a new GeneSent instance and load the welcome subject.
//         *      - Else
//         *        - Determine if the status has changed by comparing the geneSent statuses with the gene statuses.
//         *          If none have changed, skip this record and continue to the next contactGene
//         *          Else load the mouse produced subject
//         *      - Set the GeneSent statuses to the Gene statuses, build the body, and set the 'sent_at' field to null to
//         *        indicate the new gene email has not yet been sent.
//         *      - Write the GeneSent object to the gene_sent table.
//         *      - Log the activity.
//         */
//        for (ContactGene contactGene : contactGenes) {
//
//            Gene gene = genesMap.get(contactGene.getGenePk());
//
//            // Validate the gene. If it fails, log the error and continue to the next gene.
//            gene = Validator.validate(gene, errorMessages);
//            if (gene == null) {
//                continue;
//            }
//
//            boolean shouldWelcome = false;
//
//            Date now = new Date();
//            GeneSent geneSent = geneSentMap.get(contactGene.getPk());
//            if (geneSent == null) {
//
//                geneSent = new GeneSent();
//                shouldWelcome = true;
//                geneSent.setSubject(getGeneWelcomeSubject(gene));
//                geneSent.setBody(buildBody(gene, geneSent, shouldWelcome));
//                geneSent.setCreatedAt(now);
//                message = "ri-generate: send welcome message. Status: " +
//                        "null" + "->" +
//                        gene.getRiAssignmentStatusPk() + " :: " +
//
//                        "null" + "->" +
//                        gene.getRiConditionalAlleleProductionStatusPk() + " :: " +
//
//                        null + "->" +
//                        gene.getRiNullAlleleProductionStatusPk() + " :: " +
//
//                        null + "->" +
//                        gene.getRiPhenotypingStatusPk();
//
//            } else {
//
//                if ((geneSent.getAssignmentStatusPk() == gene.getRiAssignmentStatusPk()) &&
//                    (geneSent.getConditionalAlleleProductionStatusPk() == gene.getRiConditionalAlleleProductionStatusPk()) &&
//                    (geneSent.getNullAlleleProductionStatusPk() == gene.getRiNullAlleleProductionStatusPk()) &&
//                    (geneSent.getPhenotypingStatusPk() == gene.getRiPhenotypingStatusPk()))
//                {
//                    continue;       // The status hasn't changed. Continue with the next geneSent record.
//                }
//
//                geneSent.setSubject(getGeneMouseProductionSubject(gene));
//                geneSent.setBody(buildBody(gene, geneSent, shouldWelcome));
//
//                message = "ri-generate: gene status has changed. Status: " +
//                        gene.getRiAssignmentStatusPk() + "->" +
//                        geneSent.getAssignmentStatusPk() + " :: " +
//
//                        gene.getRiConditionalAlleleProductionStatusPk() + "->" +
//                        geneSent.getConditionalAlleleProductionStatusPk() + " :: " +
//
//                        gene.getRiNullAlleleProductionStatusPk() + "->" +
//                        geneSent.getNullAlleleProductionStatusPk() + " :: " +
//
//                        gene.getRiPhenotypingStatusPk() + "->" +
//                        geneSent.getPhenotypingStatusPk();
//            }
//
//            // The status has changed.
//
//            // Fill in the geneSent fields and write the record.
//            geneSent.setContactGenePk(contactGene.getPk());
//            geneSent.setAssignmentStatusPk(gene.getRiAssignmentStatusPk());
//            geneSent.setConditionalAlleleProductionStatusPk(gene.getRiConditionalAlleleProductionStatusPk());
//            geneSent.setNullAlleleProductionStatusPk(gene.getRiNullAlleleProductionStatusPk());
//            geneSent.setPhenotypingStatusPk(gene.getRiPhenotypingStatusPk());
//
//            geneSent.setSentAt(null);
//
//            geneSent = sqlUtils.insertGeneSent(geneSent);
//
//            count++;
//        }
//
//        if ( ! errorMessages.isEmpty()) {
//            // Mark these as 'info' so they do not make the build unstable. Data errors are iMits problems.
//            logger.info("WARNINGS:");
//            for (String s : errorMessages) {
//                logger.info("\t" + s);
//            }
//        }
//
//        logger.info("Run ApplicationGenerate: " + count + " emails queued for gene status changes");
//    }
//
//    public String buildBody(Gene gene, GeneSent geneSent, boolean shouldWelcome) {
//        StringBuilder body = new StringBuilder("Dear colleague,\n\n");
//
//        if (shouldWelcome) {
//
//            // 1.1
//            body
//                    .append("Thank you for registering interest in gene ")
//                    .append(gene.getSymbol())
//                    .append(".\n");
//
//        } else {
//
//            // 1.2
//            body
//                    .append("You have registered interest in gene ")
//                    .append(gene.getSymbol())
//                    .append(" via the IMPC (www.mousephenotype.org). ")
//                    .append("You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n");
//
//        }
//
//        if (gene.getRiAssignmentStatusPk() != geneSent.getAssignmentStatusPk()) {
//            if ((gene.getRiAssignmentStatusPk() != null) && (gene.getAssignmentStatus().equals(GeneStatus.PRODUCTION_AND_PHENOTYPING_PLANNED))) {
//
//                // 2.1
//                body
//                        .append("\n")
//                        .append("This gene has been selected for mouse production and phenotyping as part of the IMPC initiative.\n");
//
//            } else if ((gene.getRiAssignmentStatusPk() != null) && (gene.getAssignmentStatus().equals(GeneStatus.WITHDRAWN))) {
//
//                // 2.2
//                body
//                        .append("\n")
//                        .append("This gene has been withdrawn from mouse production and phenotyping as part of the IMPC initiative.\n");
//                ;
//
//            } else if ((gene.getRiAssignmentStatusPk() != null) && (gene.getAssignmentStatus().equals(GeneStatus.NOT_PLANNED))) {
//
//                // 2.3
//                body
//                        .append("\n")
//                        .append("This gene has not been selected for mouse production and phenotyping as part of the IMPC initiative.")
//                        .append(" This gene will be considered for mouse production in the future by the IMPC.\n");
//            }
//        }
//
//        if ((gene.getRiAssignmentStatusPk() != null) && (gene.getAssignmentStatus().equals(GeneStatus.PRODUCTION_AND_PHENOTYPING_PLANNED)) &&
//           ((gene.getRiNullAlleleProductionStatusPk() == null) || (gene.getRiNullAlleleProductionStatusPk() == 0)) &&
//           ((gene.getRiConditionalAlleleProductionStatusPk() == null) || (gene.getRiConditionalAlleleProductionStatusPk() == 0))) {
//
//            // 3.1
//            body
//                    .append("\n")
//                    .append("The IMPC initiative will aim to produce a null allele for this gene, which will enter the IMPC phenotyping pipeline.\n");
//        }
//
//        if ((gene.getRiNullAlleleProductionStatusPk() != null) && (gene.getNullAlleleProductionStatus().equals(GeneStatus.MOUSE_PRODUCTION_STARTED))) {
//
//            // 4.1
//            String startDate = (gene.getNullAlleleProductionStartDate() == null ? "<Unknown>" : sdf.format(gene.getNullAlleleProductionStartDate()));
//            body
//                    .append("\n")
//                    .append("Mouse Production for the null allele commenced on ")
//                    .append(startDate)
//                    .append(" for this gene.\n");
//
//        } else if ((gene.getRiNullAlleleProductionStatusPk() != null) && (gene.getNullAlleleProductionStatus().equals(GeneStatus.MOUSE_PRODUCED))) {
//
//            // 4.2
//            String startDate = (gene.getNullAlleleProductionStartDate() == null ? "<Unknown>" : sdf.format(gene.getNullAlleleProductionStartDate()));
//            String producedDate = (gene.getNullAlleleProductionStatusDate() == null ? "<Unknown>" : sdf.format(gene.getNullAlleleProductionStatusDate()));
//            body
//                    .append("\n")
//                    .append("Mouse Production for the null allele commenced on ")
//                    .append(startDate)
//                    .append(" and ")
//                    .append(gene.getNullAlleleProductionCentre())
//                    .append(" produced genotype confirmed mice on ")
//                    .append(producedDate)
//                    .append(".\n");
//        }
//
//        if ((gene.getRiConditionalAlleleProductionStatusPk() != null) && (gene.getConditionalAlleleProductionStatus().equals(GeneStatus.MOUSE_PRODUCTION_STARTED)) ) {
//
//            // 5.1
//            String startDate = (gene.getConditionalAlleleProductionStartDate() == null ? "<Unknown>" : sdf.format(gene.getConditionalAlleleProductionStartDate()));
//            body
//                    .append("\n")
//                    .append("Mouse Production for the conditional allele commenced on ")
//                    .append(startDate)
//                    .append(" for this gene.\n");
//
//        } else if ((gene.getRiConditionalAlleleProductionStatusPk() != null) && (gene.getConditionalAlleleProductionStatus().equals(GeneStatus.MOUSE_PRODUCED))) {
//
//            // 5.2
//            String startDate = (gene.getConditionalAlleleProductionStartDate() == null ? "<Unknown>" : sdf.format(gene.getConditionalAlleleProductionStartDate()));
//            String producedDate = (gene.getConditionalAlleleProductionStatusDate() == null ? "<Unknown" : sdf.format(gene.getConditionalAlleleProductionStatusDate()));
//            body
//                    .append("\n")
//                    .append("Mouse Production for the conditional allele commenced on ")
//                    .append(startDate)
//                    .append(" and ")
//                    .append(gene.getConditionalAlleleProductionCentre())
//                    .append(" produced genotype confirmed mice on ")
//                    .append(producedDate)
//                    .append("\n");
//        }
//
//        if (gene.getPhenotypingStatus() != null) {
//            if ((gene.getPhenotypingStatus().equals(GeneStatus.PHENOTYPING_DATA_AVAILABLE)) && (gene.getNumberOfSignificantPhenotypes() > 0)) {
//
//                // 6.1
//                body
//                        .append("\n")
//                        .append("Phenotype data for this gene is now available on the IMPC portal. The IMPC portal is now showing phenotype data ")
//                        .append("and has identified ")
//                        .append(gene.getNumberOfSignificantPhenotypes())
//                        .append(" significant phenotypes.\n");
//
//            } else if ((gene.getPhenotypingStatus().equals(GeneStatus.MORE_PHENOTYPING_DATA_AVAILABLE)) && (gene.getNumberOfSignificantPhenotypes() > 0)) {
//
//                // 6.2
//                body
//                        .append("\n")
//                        .append("Additional phenotype data for this gene has become available on the IMPC portal. Phenotype data has been collected ")
//                        .append("and the IMPC portal has identified ")
//                        .append(gene.getNumberOfSignificantPhenotypes())
//                        .append(" significant phenotypes.\n");
//            }
//        }
//
//        body
//                .append("\n")
//                .append("You will be notified by email with any future changes in this gene's status.\n")
//                .append("\n")
//                .append(getEpilogue());
//
//        return body.toString();
//    }
//
//    public String getGeneWelcomeSubject(Gene gene) {
//        String subject;
//
//        subject = "IMPC Gene registration for " + gene.getSymbol();
//
//        return subject;
//    }
//
//
//    public String getGeneMouseProductionSubject(Gene gene) {
//        String subject;
//
//        subject = "IMPC Status update for " + gene.getSymbol();
//
//        return subject;
//    }
//
//
//    // PRIVATE METHODS
//
//
//    private String getEpilogue() {
//        StringBuilder body = new StringBuilder();
//
//        body
//                .append("For further information / enquiries please write to ")
//                .append("mouse-helpdesk@ebi.ac.uk.\n")
//                .append("\n")
//                .append("Best Regards,\n")
//                .append("\n")
//                .append("The MPI2 (KOMP2) informatics consortium");
//
//        return body.toString();
    }


    // PROTECTED METHODS


    protected OptionSet parseOptions(String[] args) {

        OptionParser parser = new OptionParser();
        OptionSet options;

        parser.allowsUnrecognizedOptions();
        parser.accepts(WELCOME_ARG);
        parser.accepts(SUMMARY_ARG).withOptionalArg();

        options = parser.parse(args);
        if (options.has(WELCOME_ARG)) {
            createWelcomeText = true;
        }
        if (options.has(SUMMARY_ARG)) {
            createSummaryTextFor = (String) options.valueOf(SUMMARY_ARG);
            createSummaryText = true;
        }

        return options;
    }
}