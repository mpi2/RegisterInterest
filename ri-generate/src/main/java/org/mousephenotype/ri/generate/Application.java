package org.mousephenotype.ri.generate;

import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.*;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.mousephenotype.ri.generate.config.AppConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by mrelac on 05/06/2017.
 *
 * This class is intended to be a command-line callable java main program that generates e-mails to contacts registered
 * for insterest in specific genes whose status indicates the gene state has changed.
 */
@SpringBootApplication
@Import( {AppConfig.class })
public class Application implements CommandLineRunner {

    @Autowired
    private DataSource riDataSource;

    @Autowired
    private SqlUtils sqlUtils;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<GeneContact> geneContacts;
    private Map<Integer, Gene> genesMap;
    private Map<Integer, GeneSent> geneSentMap;
    private Map<String, GeneStatus> statusMap;      // keyed by status

    // These can't be marked final but they are only to be written to once. They are the primary keys.
    private Integer STATUS_MORE_PHENOTYPE_DATA_AVAILABLE_PK;
    private Integer STATUS_MOUSE_PRODUCED_PK;
    private Integer STATUS_MOUSE_PRODUCTION_STARTED_PK;
    private Integer STATUS_NOT_PLANNED_PK;
    private Integer STATUS_PHENOTYPE_DATA_AVAILABLE_PK;
    private Integer STATUS_PRODUCTION_AND_PHENOTYPING_PLANNED_PK;
    private Integer STATUS_REGISTER_PK;
    private Integer STATUS_UNREGISTER_PK;
    private Integer STATUS_WITHDRAWN_PK;


    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM yyyy");


    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }


    @PostConstruct
    public void initialise() {
        geneContacts = sqlUtils.getGeneContacts();
        genesMap = sqlUtils.getGenesByPk();
        geneSentMap = sqlUtils.getGeneSent();
        statusMap = sqlUtils.getStatusMap();

        STATUS_MORE_PHENOTYPE_DATA_AVAILABLE_PK = statusMap.get("more_phenotyping_data_available").getPk();
        STATUS_MOUSE_PRODUCED_PK = statusMap.get("mouse_produced").getPk();
        STATUS_MOUSE_PRODUCTION_STARTED_PK = statusMap.get("mouse_production_started").getPk();
        STATUS_NOT_PLANNED_PK = statusMap.get("not_planned").getPk();
        STATUS_PHENOTYPE_DATA_AVAILABLE_PK = statusMap.get("phenotyping_data_available").getPk();
        STATUS_PRODUCTION_AND_PHENOTYPING_PLANNED_PK = statusMap.get("production_and_phenotyping_planned").getPk();
        STATUS_REGISTER_PK = statusMap.get("register").getPk();
        STATUS_UNREGISTER_PK = statusMap.get("unregister").getPk();
        STATUS_WITHDRAWN_PK = statusMap.get("withdrawn").getPk();
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

            geneSent = sqlUtils.updateOrInsertGeneEmailQueued(geneSent);
            
            sqlUtils.logGeneStatusChangeAction(geneSent, geneContact.getContactPk(), geneContact.getGenePk(), message);
            count++;
        }

        logger.info("Run ri-generate: " + count + " emails queued for gene status changes");
    }

    public String buildBody(Gene gene, GeneSent geneSent, boolean shouldWelcome) {
        StringBuilder body = new StringBuilder("Dear colleague,\n\n");

        if (shouldWelcome) {
            body
                    .append("Thank you for registering interest in gene ")
                    .append(gene.getSymbol())
                    .append(".\n")
                    .append("\n");

        } else if ((gene.getPhenotypingStatusPk() != null) && (gene.getPhenotypingStatusPk() > 0)) {

            body
                    .append("You have registered interest in gene ")
                    .append(gene.getSymbol())
                    .append(" via the IMPC (")
                    .append("<a href=www.mousephenotype.org>www.mousephenotype.org</a>). ")
                    .append("You are receiving this email because the IMPC phenotyping status of the gene has changed.\n")
                    .append("\n");
        } else if ((gene.getAssignmentStatusPk() != null) && (gene.getAssignmentStatusPk() > 0)) {
            body
                    .append("You have registered interest in gene ")
                    .append(gene.getSymbol())
                    .append(" via the IMPC (")
                    .append("<a href=www.mousephenotype.org>www.mousephenotype.org</a>). ")
                    .append("You are receiving this email because the IMPC production status of the gene has changed.\n")
                    .append("\n");
        }

        if (gene.getAssignmentStatusPk() != geneSent.getAssignmentStatusPk()) {
            if ((gene.getAssignmentStatusPk() != null) && (gene.getAssignmentStatusPk() == STATUS_PRODUCTION_AND_PHENOTYPING_PLANNED_PK)) {
                body
                        .append("This gene has been selected for mouse production and phenotyping as part of the IMPC initiative.\n")
                        .append("\n");

            } else if ((gene.getAssignmentStatusPk() != null) && (gene.getAssignmentStatusPk() == STATUS_WITHDRAWN_PK)) {
                body
                        .append("This gene has been withdrawn from mouse production and phenotyping as part of the IMPC initiative.\n")
                        .append("\n");
                ;

            } else if ((gene.getAssignmentStatusPk() != null) && (gene.getAssignmentStatusPk() == STATUS_NOT_PLANNED_PK)) {
                body
                        .append("This gene has not been selected for mouse production and phenotyping as part of the IMPC initiative.")
                        .append(" This gene will be considered for mouse production in the future by the IMPC.\n")
                        .append("\n");
            }
        }

        if ((gene.getAssignmentStatusPk() != null) && (gene.getAssignmentStatusPk() == STATUS_PRODUCTION_AND_PHENOTYPING_PLANNED_PK) &&
           ((gene.getNullAlleleProductionStatusPk() == null) || (gene.getNullAlleleProductionStatusPk() == 0)) &&
           ((gene.getConditionalAlleleProductionStatusPk() == null) || (gene.getConditionalAlleleProductionStatusPk() == 0))) {

            body
                    .append("The IMPC initiative will aim to produce a null allele for this gene, which will enter the IMPC phenotyping pipeline. Please")
                    .append(" contact the production centre ")
                    .append(gene.getAssignedTo())
                    .append(" if you are interested in the conditional allele. A fee for service maybe offered by ")
                    .append(gene.getAssignedTo())
                    .append(" for producing more complex alleles such as point mutation alleles.\n")
                    .append("\n");;
        }

        if ((gene.getNullAlleleProductionStatusPk() != null) && (gene.getNullAlleleProductionStatusPk() == STATUS_MOUSE_PRODUCTION_STARTED_PK) &&
            (gene.getNullAlleleProductionStatusDate() != null)) {

            String datePiece = sdf.format(gene.getNullAlleleProductionStatusDate());
            body
                    .append("Mouse Production for the null allele commenced on ")
                    .append(datePiece)
                    .append(" for this gene.\n")
                    .append("\n");;
        }

        if ((gene.getNullAlleleProductionStatusPk() != null) && (gene.getNullAlleleProductionStatusPk() == STATUS_MOUSE_PRODUCED_PK) &&
                (gene.getNullAlleleProductionStatusDate() != null)) {

            String datePiece = sdf.format(gene.getNullAlleleProductionStatusDate());
            body
                    .append("Genotype confirmed mice were produced at ")
                    .append(gene.getNullAlleleProductionCentre())
                    .append(" on ")
                    .append(datePiece)
                    .append(".\n")
                    .append("\n");;
        }

        if ((gene.getConditionalAlleleProductionStatusPk() != null) && (gene.getConditionalAlleleProductionStatusPk() == STATUS_MOUSE_PRODUCTION_STARTED_PK) &&
            (gene.getConditionalAlleleProductionStatusDate() != null)) {

            String datePiece = sdf.format(gene.getConditionalAlleleProductionStatusDate());
            body
                    .append("Mouse Production for the conditional allele commenced on ")
                    .append(datePiece)
                    .append(" for this gene.\n")
                    .append("\n");;
        }

        if ((gene.getConditionalAlleleProductionStatusPk() != null) && (gene.getConditionalAlleleProductionStatusPk() == STATUS_MOUSE_PRODUCED_PK) &&
                (gene.getConditionalAlleleProductionStatusDate() != null)) {

            String datePiece = sdf.format(gene.getConditionalAlleleProductionStatusDate());
            body
                    .append("Genotype confirmed mice were produced at ")
                    .append(gene.getConditionalAlleleProductionCentre())
                    .append(" on ")
                    .append(datePiece)
                    .append(".\n")
                    .append("\n");
        }

        if ((gene.getPhenotypingStatusPk() == STATUS_PHENOTYPE_DATA_AVAILABLE_PK) && (gene.getNumberOfSignificantPhenotypes() > 0)) {
            body
                    .append("Phenotype data for this gene is now available on the IMPC portal. The IMPC portal is now showing phenotype data ")
                    .append("and has identified ")
                    .append(gene.getNumberOfSignificantPhenotypes())
                    .append(" significant phenotypes.\n")
                    .append("\n");

        } else if ((gene.getPhenotypingStatusPk() == STATUS_MORE_PHENOTYPE_DATA_AVAILABLE_PK) && (gene.getNumberOfSignificantPhenotypes() > 0)) {
            body
                    .append("Additional phenotype data for this gene has become available on the IMPC portal. Phenotype data has been collected ")
                    .append("and the IMPC portal has identified ")
                    .append(gene.getNumberOfSignificantPhenotypes())
                    .append(" significant phenotypes.\n")
                    .append("\n");
        }

        body
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

        geneSent = sqlUtils.updateOrInsertGeneEmailQueued(geneSent);

        String message = "ri-generate: send unregister message";
        sqlUtils.logGeneStatusChangeAction(geneSent, geneContact.getContactPk(), geneContact.getGenePk(), message);

        return 1;
    }

    private String getEpilogue() {
        StringBuilder body = new StringBuilder();

        body
                .append("For further information / enquiries please write to ")
                .append("<a href=mailto:mouse-helpdesk@ebi.ac.uk>mouse-helpdesk@ebi.ac.uk</a>).\n")
                .append("\n")
                .append("Best Regards,\n")
                .append("\n")
                .append("The MPI2 (KOMP2) informatics consortium\n");

        return body.toString();
    }
}