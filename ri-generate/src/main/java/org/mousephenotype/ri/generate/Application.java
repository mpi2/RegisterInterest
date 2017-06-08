package org.mousephenotype.ri.generate;

import org.mousephenotype.ri.core.SqlUtils;
import org.mousephenotype.ri.core.entities.*;
import org.mousephenotype.ri.generate.config.AppConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;


/**
 * Created by mrelac on 05/06/2017.
 *
 * This class is intended to be a command-line callable java main program that generates e-mails to contacts registered
 * for insterest in specific genes whose status indicates the gene state has changed.
 */
@SpringBootApplication
@ComponentScan({"org.mousephenotype"})
@Import( {AppConfig.class })
public class Application implements CommandLineRunner {

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }

    @Autowired
    public NamedParameterJdbcTemplate jdbc;

    @Autowired
    private DataSource riDataSource;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private SqlUtils sqlUtils = new SqlUtils(jdbc);


    private List<GeneContact> geneContacts;
    private Map<Integer, Gene> genesMap;
    private Map<Integer, GeneSent> sentMap;
    private Map<String, GeneStatus> statusMap;      // keyed by status

    // These can't be marked final but they are only to be written to once. They are the primary keys.
    private int COMPONENT_GENE_PK;

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

    @PostConstruct
    public void initialise() {
        geneContacts = sqlUtils.getGeneContacts();
        genesMap = sqlUtils.getGenesByPk();
        sentMap = sqlUtils.getSent();
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


        /**
         * For each geneContact:
         *      - Get the GeneSent object from the sentMap using the contactGene primary key. If it is not found, create a
         *        new GeneSent instance with 'register' status.
         *      - Look up the Gene object from genesMap using the gene primary key (found in the contactGene object)
         *      - Determine if the status has changed by calling createNewSent(). If the returned GeneSent object is null,
         *        the status hasn't changed and we skip to the next contactGene; otherwise, put the GeneSent object in the
         *        toBeSent list.
         *
         *  Walk the toBeSent list, sending each e-mail
         */
        for (GeneContact contactGene : geneContacts) {

            GeneSent geneSent = sentMap.get(contactGene.getPk());
            if (geneSent == null) {
                geneSent = new GeneSent();
          //      geneSent.setContactGenePk(contactGene.getPk());
          //      geneSent.setContactGenePk(COMPONENT_GENE_PK);
          //      geneSent.setStatusPk(STATUS_REGISTER_PK);
            }

            Gene gene = genesMap.get(contactGene.getGenePk());

            GeneSent newGeneSent = createNewSent(geneSent, gene);
            if (newGeneSent == null) {
                continue;
            }
        }
    }

    /**
     * Using the {@link GeneSent} and {@link Gene} instances, determine if a n e-mail is to be geneSent and, if so, populate
     * a new {@link GeneSent} object and return it. If not, return null.
     *
     * @param geneSent The {@link GeneSent} instance with the last geneSent state information
     * @param gene The {@link Gene} instance with the new state information
     *
     * @return A fully-populated {@link GeneSent} instance, if an e-mail is to be geneSent; null otherwise
     */
    private GeneSent createNewSent(GeneSent geneSent, Gene gene) {

        GeneSent newGeneSent = new GeneSent();
      //  newGeneSent.setContactGenePk(geneSent.getContactGenePk());

//        if (geneSent.getGeneStatusPk() == STATUS_REGISTER_PK) {
//
//            // Register Interest Gene State
//            newGeneSent.setGeneStatusPk(gene.getAssignmentStatusPk());
//            newGeneSent.setSubject("");
//            newGeneSent.setBody(buildMouseProductionGeneBody(gene));
//
//            return newGeneSent;
//
//        } else if ((geneSent.getGeneStatusPk() == STATUS_NOT_PLANNED_PK) && (gene.getNullAlleleProductionStatusPk() == STATUS_PRODUCTION_AND_PHENOTYPING_PLANNED_PK) ||
//                   (geneSent.getGeneStatusPk() == STATUS_NOT_PLANNED_PK) && (gene.getNullAlleleProductionStatusPk() == STATUS_PRODUCTION_AND_PHENOTYPING_PLANNED_PK) ||
//                   (geneSent.getGeneStatusPk() == STATUS_NOT_PLANNED_PK) && (gene.getNullAlleleProductionStatusPk() == STATUS_PRODUCTION_AND_PHENOTYPING_PLANNED_PK) ||
//                   (geneSent.getGeneStatusPk() == STATUS_NOT_PLANNED_PK) && (gene.getNullAlleleProductionStatusPk() == STATUS_PRODUCTION_AND_PHENOTYPING_PLANNED_PK) ||
//                   ()) {
//
//            if (gene.getNullAlleleProductionStatusPk() == STATUS_PRODUCTION_AND_PHENOTYPING_PLANNED_PK) {
//
//            }
//        }
//
//
//
//
//
//
//        // GeneStatus has changed. e-mail is to be geneSent.
//        newGeneSent =


        return newGeneSent;
    }




    public String buildMouseProductionGeneBody(Gene gene) {
        StringBuilder body = new StringBuilder("Dear colleague,\n");

        if (gene.getAssignmentStatusPk() == STATUS_REGISTER_PK) {
            body
                    .append("Thank you for registering interest in gene ")
                    .append(gene.getSymbol())
                    .append(".\n");
        } else if (gene.getAssignmentStatusPk() == STATUS_UNREGISTER_PK) {
            body
                    .append("You have been unregistered for interest in gene ")
                    .append(gene.getSymbol())
                    .append(".\n");
        }

        if ((gene.getAssignmentStatusPk() != null) && (gene.getAssignmentStatusPk() > 0)) {
            body
                    .append("You have registered interest in gene ")
                    .append(gene.getSymbol())
                    .append(" via the IMPC (")
                    .append("<a href=www.mousephenotype.org>www.mousephenotype.org</a>).\n")
                    .append("You are receiving this email because the IMPC production status of the gene has changed.\n");
        }

        if ((gene.getAssignmentStatusPk() != null) && (gene.getAssignmentStatusPk() == STATUS_PRODUCTION_AND_PHENOTYPING_PLANNED_PK)) {
            body
                    .append("This gene has been selected for mouse production and phenotyping as part of the IMPC initiative.\n");

        } else if ((gene.getAssignmentStatusPk() != null) && (gene.getAssignmentStatusPk() == STATUS_WITHDRAWN_PK)) {
            body
                    .append("This gene has been withdrawn from mouse production and phenotyping as part of the IMPC initiative.\n");

        } else if ((gene.getAssignmentStatusPk() != null) && (gene.getAssignmentStatusPk() == STATUS_NOT_PLANNED_PK)) {
            body
                    .append("This gene has not been selected for mouse production and phenotyping as part of the IMPC initiative.")
                    .append(" This gene will be considered for mouse production in the future by the IMPC.\n");
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
                    .append(" for producing more complex alleles such as point mutation alleles.\n");
        }

        if ((gene.getNullAlleleProductionStatusPk() != null) && (gene.getNullAlleleProductionStatusPk() == STATUS_MOUSE_PRODUCTION_STARTED_PK) &&
            (gene.getNullAlleleProductionStatusDate() != null)) {

            String datePiece = sdf.format(gene.getNullAlleleProductionStatusDate());
            body
                    .append("Mouse Production for the null allele commenced on ")
                    .append(datePiece)
                    .append(" for this gene.\n");
        }

        if ((gene.getNullAlleleProductionStatusPk() != null) && (gene.getNullAlleleProductionStatusPk() == STATUS_MOUSE_PRODUCED_PK) &&
                (gene.getNullAlleleProductionStatusDate() != null)) {

            String datePiece = sdf.format(gene.getNullAlleleProductionStatusDate());
            body
                    .append("Genotype confirmed mice with the null allele ")
//                    .append(gene.getNullAlleleSymbol())
                    .append(" were produced at ")
                    .append(gene.getNullAlleleProductionCentre())
                    .append(" on ")
                    .append(datePiece)
                    .append(".\n");
        }

        if ((gene.getConditionalAlleleProductionStatusPk() != null) && (gene.getConditionalAlleleProductionStatusPk() == STATUS_MOUSE_PRODUCTION_STARTED_PK) &&
            (gene.getConditionalAlleleProductionStatusDate() != null)) {

            String datePiece = sdf.format(gene.getConditionalAlleleProductionStatusDate());
            body
                    .append("Mouse Production for the conditional allele commenced on ")
                    .append(datePiece)
                    .append(" for this gene.\n");
        }

        if ((gene.getConditionalAlleleProductionStatusPk() != null) && (gene.getConditionalAlleleProductionStatusPk() == STATUS_MOUSE_PRODUCED_PK) &&
                (gene.getConditionalAlleleProductionStatusDate() != null)) {

            String datePiece = sdf.format(gene.getConditionalAlleleProductionStatusDate());
            body
                    .append("Genotype confirmed mice with the conditional allele ")
//                    .append(gene.getConditionalAlleleSymbol())
                    .append(" were produced at ")
                    .append(gene.getConditionalAlleleProductionCentre())
                    .append(" on ")
                    .append(datePiece)
                    .append(".\n");
        }

        if (gene.getAssignmentStatusPk() == STATUS_UNREGISTER_PK) {
            body
                    .append("You will no longer be notified about any future changes in this gene's status.\n");

        } else {
            body
                    .append("You will be notified by email with any future changes in this gene's status.\n");
        }

        body
                .append("For further information / enquiries please write to ")
                .append("<a href=mailto:mouse-helpdesk@ebi.ac.uk>mouse-helpdesk@ebi.ac.uk</a>).\n")
                .append("Best Regards,\n")
                .append("The MPI2 (KOMP2) informatics consortium\n");

        return body.toString();
    }


    public String buildMousePhenotypeGeneBody(Gene gene) {
        StringBuilder body = new StringBuilder("Dear colleague,\n");

        if ((gene.getPhenotypingStatusPk() != null) && (gene.getPhenotypingStatusPk() > 0)) {
            body
                    .append("You have registered interest in gene ")
                    .append(gene.getSymbol())
                    .append(" via the IMPC (")
                    .append("<a href=www.mousephenotype.org>www.mousephenotype.org</a>).\n")
                    .append("You are receiving this email because the IMPC phenotyping status of the gene has changed.\n");
        }

        if (gene.getPhenotypingStatusPk() == STATUS_PHENOTYPE_DATA_AVAILABLE_PK) {
            body
                    .append("Phenotype data for this gene is now available on the IMPC portal. The IMPC portal is now showing phenotype data for ");

        } else if (gene.getPhenotypingStatusPk() == STATUS_MORE_PHENOTYPE_DATA_AVAILABLE_PK) {
            body
                    .append("Additional phenotype data for this gene has become available on the IMPC portal. Phenotype data has been collected for ");
        }

        body
                .append("Additional phenotype data for this gene has become available on the IMPC portal. Phenotype data has been collected for ")
//                .append(gene.getProcedureCount())
                .append(" procedures and has identified ")
                .append(gene.getNumberOfSignificantPhenotypes())
                .append("significant phenotypes.\n");

        body
                .append("For further information / enquiries please write to ")
                .append("<a href=mailto:mouse-helpdesk@ebi.ac.uk>mouse-helpdesk@ebi.ac.uk</a>).\n")
                .append("Best Regards,\n")
                .append("The MPI2 (KOMP2) informatics consortium\n");



        return body.toString();
    }


    public String getSubject() {
        String subject;

        subject = "SUBJECT GOES HERE";

        return subject;
    }
}