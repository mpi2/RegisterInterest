package org.mousephenotype.ri.generate;

import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.Validator;
import org.mousephenotype.ri.core.entities.Gene;
import org.mousephenotype.ri.core.entities.GeneContact;
import org.mousephenotype.ri.core.entities.GeneSent;
import org.mousephenotype.ri.core.entities.GeneStatus;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
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
@SpringBootApplication
public class ApplicationGenerate implements CommandLineRunner {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM yyyy");

    private SqlUtils sqlUtils;

    public final Set<String> errorMessages = ConcurrentHashMap.newKeySet();
    private List<GeneContact> geneContacts;
    private Map<Integer, Gene> genesMap;
    private Map<Integer, GeneSent> geneSentMap;


    @Inject
    public ApplicationGenerate(SqlUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }


    @PostConstruct
    public void initialise() {
        geneContacts = sqlUtils.getGeneContacts();
        genesMap = sqlUtils.getGenesByPk();
        geneSentMap = sqlUtils.getGenesSent();
    }


    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ApplicationGenerate.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }


    @Override
    public void run(String... args) throws Exception {

        int count = 0;
        String message;
        
        /**
         * For each geneContact:
         *      - Look up the Gene object from genesMap using the gene primary key (found in the geneContact object)
         *      - Get the GeneSent object from the geneSentMap using the geneContact primary key.
         *      - If it is not found
         *          - create a new GeneSent instance and load the welcome subject.
         *      - Else
         *        - Determine if the status has changed by comparing the geneSent statuses with the gene statuses.
         *          If none have changed, skip this record and continue to the next geneContact
         *          Else load the mouse produced subject
         *      - Set the GeneSent statuses to the Gene statuses, build the body, and set the 'sent_at' field to null to
         *        indicate the new gene email has not yet been sent.
         *      - Write the GeneSent object to the gene_sent table.
         *      - Log the activity.
         */
        for (GeneContact geneContact : geneContacts) {

            Gene gene = genesMap.get(geneContact.getGenePk());

            // Validate the gene. If it fails, log the error and continue to the next gene.
            gene = Validator.validate(gene, errorMessages);
            if (gene == null) {
                continue;
            }

            boolean shouldWelcome = false;

            Date now = new Date();
            GeneSent geneSent = geneSentMap.get(geneContact.getPk());
            if (geneSent == null) {

                geneSent = new GeneSent();
                shouldWelcome = true;
                geneSent.setSubject(getGeneWelcomeSubject(gene));
                geneSent.setBody(buildBody(gene, geneSent, shouldWelcome));
                geneSent.setCreatedAt(now);
                message = "ri-generate: send welcome message. Status: " +
                        "null" + "->" +
                        gene.getAssignmentStatusPk() + " :: " +

                        "null" + "->" +
                        gene.getConditionalAlleleProductionStatusPk() + " :: " +

                        null + "->" +
                        gene.getNullAlleleProductionStatusPk() + " :: " +

                        null + "->" +
                        gene.getPhenotypingStatusPk();

            } else {

                if ((geneSent.getAssignmentStatusPk() == gene.getAssignmentStatusPk()) &&
                    (geneSent.getConditionalAlleleProductionStatusPk() == gene.getConditionalAlleleProductionStatusPk()) &&
                    (geneSent.getNullAlleleProductionStatusPk() == gene.getNullAlleleProductionStatusPk()) &&
                    (geneSent.getPhenotypingStatusPk() == gene.getPhenotypingStatusPk()))
                {
                    continue;       // The status hasn't changed. Continue with the next geneSent record.
                }

                geneSent.setSubject(getGeneMouseProductionSubject(gene));
                geneSent.setBody(buildBody(gene, geneSent, shouldWelcome));

                message = "ri-generate: gene status has changed. Status: " +
                        gene.getAssignmentStatusPk() + "->" +
                        geneSent.getAssignmentStatusPk() + " :: " +

                        gene.getConditionalAlleleProductionStatusPk() + "->" +
                        geneSent.getConditionalAlleleProductionStatusPk() + " :: " +

                        gene.getNullAlleleProductionStatusPk() + "->" +
                        geneSent.getNullAlleleProductionStatusPk() + " :: " +

                        gene.getPhenotypingStatusPk() + "->" +
                        geneSent.getPhenotypingStatusPk();
            }

            // The status has changed.

            // Fill in the geneSent fields and write the record.
            geneSent.setGeneContactPk(geneContact.getPk());
            geneSent.setAssignmentStatusPk(gene.getAssignmentStatusPk());
            geneSent.setConditionalAlleleProductionStatusPk(gene.getConditionalAlleleProductionStatusPk());
            geneSent.setNullAlleleProductionStatusPk(gene.getNullAlleleProductionStatusPk());
            geneSent.setPhenotypingStatusPk(gene.getPhenotypingStatusPk());

            geneSent.setSentAt(null);

            geneSent = sqlUtils.updateOrInsertGeneSent(geneSent);
            
            sqlUtils.logGeneStatusChangeAction(geneSent, geneContact.getContactPk(), geneContact.getGenePk(), message);
            count++;
        }

        if ( ! errorMessages.isEmpty()) {
            logger.warn("WARNINGS:");
            for (String s : errorMessages) {
                logger.warn("\t" + s);
            }
        }

        logger.info("Run ri-generate: " + count + " emails queued for gene status changes");
    }

    public String buildBody(Gene gene, GeneSent geneSent, boolean shouldWelcome) {
        StringBuilder body = new StringBuilder("Dear colleague,\n\n");

        if (shouldWelcome) {

            // 1.1
            body
                    .append("Thank you for registering interest in gene ")
                    .append(gene.getSymbol())
                    .append(".\n");

        } else {

            // 1.2
            body
                    .append("You have registered interest in gene ")
                    .append(gene.getSymbol())
                    .append(" via the IMPC (www.mousephenotype.org). ")
                    .append("You are receiving this email because either the IMPC production or phenotyping status of the gene has changed.\n");

        }

        if (gene.getAssignmentStatusPk() != geneSent.getAssignmentStatusPk()) {
            if ((gene.getAssignmentStatusPk() != null) && (gene.getAssignmentStatus().equals(GeneStatus.PRODUCTION_AND_PHENOTYPING_PLANNED))) {

                // 2.1
                body
                        .append("\n")
                        .append("This gene has been selected for mouse production and phenotyping as part of the IMPC initiative.\n");

            } else if ((gene.getAssignmentStatusPk() != null) && (gene.getAssignmentStatus().equals(GeneStatus.WITHDRAWN))) {

                // 2.2
                body
                        .append("\n")
                        .append("This gene has been withdrawn from mouse production and phenotyping as part of the IMPC initiative.\n");
                ;

            } else if ((gene.getAssignmentStatusPk() != null) && (gene.getAssignmentStatus().equals(GeneStatus.NOT_PLANNED))) {

                // 2.3
                body
                        .append("\n")
                        .append("This gene has not been selected for mouse production and phenotyping as part of the IMPC initiative.")
                        .append(" This gene will be considered for mouse production in the future by the IMPC.\n");
            }
        }

        if ((gene.getAssignmentStatusPk() != null) && (gene.getAssignmentStatus().equals(GeneStatus.PRODUCTION_AND_PHENOTYPING_PLANNED)) &&
           ((gene.getNullAlleleProductionStatusPk() == null) || (gene.getNullAlleleProductionStatusPk() == 0)) &&
           ((gene.getConditionalAlleleProductionStatusPk() == null) || (gene.getConditionalAlleleProductionStatusPk() == 0))) {

            // 3.1
            body
                    .append("\n")
                    .append("The IMPC initiative will aim to produce a null allele for this gene, which will enter the IMPC phenotyping pipeline.\n");
        }

        if ((gene.getNullAlleleProductionStatusPk() != null) && (gene.getNullAlleleProductionStatus().equals(GeneStatus.MOUSE_PRODUCTION_STARTED))) {

            // 4.1
            String startDate = (gene.getNullAlleleProductionStartDate() == null ? "<Unknown>" : sdf.format(gene.getNullAlleleProductionStartDate()));
            body
                    .append("\n")
                    .append("Mouse Production for the null allele commenced on ")
                    .append(startDate)
                    .append(" for this gene.\n");

        } else if ((gene.getNullAlleleProductionStatusPk() != null) && (gene.getNullAlleleProductionStatus().equals(GeneStatus.MOUSE_PRODUCED))) {

            // 4.2
            String startDate = (gene.getNullAlleleProductionStartDate() == null ? "<Unknown>" : sdf.format(gene.getNullAlleleProductionStartDate()));
            String producedDate = (gene.getNullAlleleProductionCompletedDate() == null ? "<Unknown>" : sdf.format(gene.getNullAlleleProductionCompletedDate()));
            body
                    .append("\n")
                    .append("Mouse Production for the null allele commenced on ")
                    .append(startDate)
                    .append(" and ")
                    .append(gene.getNullAlleleProductionCentre())
                    .append(" produced genotype confirmed mice on ")
                    .append(producedDate)
                    .append(".\n");
        }

        if ((gene.getConditionalAlleleProductionStatusPk() != null) && (gene.getConditionalAlleleProductionStatus().equals(GeneStatus.MOUSE_PRODUCTION_STARTED)) ) {

            // 5.1
            String startDate = (gene.getConditionalAlleleProductionStartDate() == null ? "<Unknown>" : sdf.format(gene.getConditionalAlleleProductionStartDate()));
            body
                    .append("\n")
                    .append("Mouse Production for the conditional allele commenced on ")
                    .append(startDate)
                    .append(" for this gene.\n");

        } else if ((gene.getConditionalAlleleProductionStatusPk() != null) && (gene.getConditionalAlleleProductionStatus().equals(GeneStatus.MOUSE_PRODUCED))) {

            // 5.2
            String startDate = (gene.getConditionalAlleleProductionStartDate() == null ? "<Unknown>" : sdf.format(gene.getConditionalAlleleProductionStartDate()));
            String producedDate = (gene.getConditionalAlleleProductionCompletedDate() == null ? "<Unknown" : sdf.format(gene.getConditionalAlleleProductionCompletedDate()));
            body
                    .append("\n")
                    .append("Mouse Production for the conditional allele commenced on ")
                    .append(startDate)
                    .append(" and ")
                    .append(gene.getConditionalAlleleProductionCentre())
                    .append(" produced genotype confirmed mice on ")
                    .append(producedDate)
                    .append("\n");
        }

        if (gene.getPhenotypingStatus() != null) {
            if ((gene.getPhenotypingStatus().equals(GeneStatus.PHENOTYPING_DATA_AVAILABLE)) && (gene.getNumberOfSignificantPhenotypes() > 0)) {

                // 6.1
                body
                        .append("\n")
                        .append("Phenotype data for this gene is now available on the IMPC portal. The IMPC portal is now showing phenotype data ")
                        .append("and has identified ")
                        .append(gene.getNumberOfSignificantPhenotypes())
                        .append(" significant phenotypes.\n");

            } else if ((gene.getPhenotypingStatus().equals(GeneStatus.MORE_PHENOTYPING_DATA_AVAILABLE)) && (gene.getNumberOfSignificantPhenotypes() > 0)) {

                // 6.2
                body
                        .append("\n")
                        .append("Additional phenotype data for this gene has become available on the IMPC portal. Phenotype data has been collected ")
                        .append("and the IMPC portal has identified ")
                        .append(gene.getNumberOfSignificantPhenotypes())
                        .append(" significant phenotypes.\n");
            }
        }

        body
                .append("\n")
                .append("You will be notified by email with any future changes in this gene's status.\n")
                .append("\n")
                .append(getEpilogue());

        return body.toString();
    }

    public String getGeneWelcomeSubject(Gene gene) {
        String subject;

        subject = "IMPC Gene registration for " + gene.getSymbol();

        return subject;
    }


    public String getGeneMouseProductionSubject(Gene gene) {
        String subject;

        subject = "IMPC Status update for " + gene.getSymbol();

        return subject;
    }

    public int generateUnregisterGeneEmail(Gene gene, GeneContact geneContact) throws InterestException {
        GeneSent geneSent = new GeneSent();
        StringBuilder body = new StringBuilder();

        body
                .append("Dear colleague,\n")
                .append("\n")
                .append("You have been unregistered for interest in gene ")
                .append(gene.getSymbol())
                .append(".\n")
                .append("\n")
                .append("You will no longer be notified about any future changes in this gene's status.\n")
                .append("\n")
                .append(getEpilogue());

        String subject = "IMPC Gene unregistration for " + gene.getSymbol();

        geneSent.setSentAt(null);
        geneSent.setSubject(subject);
        geneSent.setBody(body.toString());
        geneSent.setGeneContactPk(geneContact.getPk());

        geneSent = sqlUtils.updateOrInsertGeneSent(geneSent);

        String message = "ri-generate: send unregister message";
        sqlUtils.logGeneStatusChangeAction(geneSent, geneContact.getContactPk(), geneContact.getGenePk(), message);

        return 1;
    }

    private String getEpilogue() {
        StringBuilder body = new StringBuilder();

        body
                .append("For further information / enquiries please write to ")
                .append("mouse-helpdesk@ebi.ac.uk.\n")
                .append("\n")
                .append("Best Regards,\n")
                .append("\n")
                .append("The MPI2 (KOMP2) informatics consortium\n");

        return body.toString();
    }
}