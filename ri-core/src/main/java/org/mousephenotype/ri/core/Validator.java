package org.mousephenotype.ri.core;

import org.mousephenotype.ri.core.entities.Gene;
import org.mousephenotype.ri.core.entities.GeneStatus;

import java.util.Set;

/**
 * This class encapsulates the code and data necessary to validate any special rules for email sending.
 *
 * Created by mrelac on 07/07/2017.
 */
public class Validator {

    public static Gene validate(Gene gene, Set<String> errMessages) {

        // if assignment_status == not_planned (NP)
        if (gene.getAssignmentStatus().equals(GeneStatus.NOT_PLANNED)) {
            if ((gene.getConditionalAlleleProductionStatusPk() != null) ||
                (gene.getNullAlleleProductionStatusPk() != null) ||
                (gene.getPhenotypingStatusPk() != null) ||
                (gene.getNumberOfSignificantPhenotypes() > 0))
            {
                errMessages.add("Data Error for " + gene.getMgiAccessionId() + ": assignmentStatus = Not Planned and other statuses/counts are not empty");
                return null;
            }
        }

        // if assignment_status == Withdrawn (W)
        if (gene.getAssignmentStatus().equals(GeneStatus.WITHDRAWN)) {
            if ((gene.getConditionalAlleleProductionStatusPk() != null) ||
                (gene.getNullAlleleProductionStatusPk() != null) ||
                (gene.getPhenotypingStatusPk() != null) ||
                (gene.getNumberOfSignificantPhenotypes() > 0))
            {
                errMessages.add("Data Error for " + gene.getMgiAccessionId() + ": assignmentStatus = Withdrawn and other statuses/counts are not empty");
                return null;
            }
        }

        // if assignment_status == Production And Phenotyping Planned (PAPP) AND phenotyping_status NOT empty
        if ((gene.getAssignmentStatus().equals(GeneStatus.PRODUCTION_AND_PHENOTYPING_PLANNED)) &&
                (gene.getPhenotypingStatusPk() != null)) {
            if (((gene.getConditionalAlleleProductionStatusPk() != null) && (gene.getConditionalAlleleProductionStatus().equals(GeneStatus.MOUSE_PRODUCED))) ||
                ((gene.getNullAlleleProductionStatusPk() != null) && (gene.getNullAlleleProductionStatus().equals(GeneStatus.MOUSE_PRODUCED))))
            {
                errMessages.add("Data Error for " + gene.getMgiAccessionId() + ": assignmentStatus = PAPP and production status = Mouse Produced");
                return null;
            }
        }

        // mrelac (2017-07-07)
        // TEMPORARY RULE: DO NOT SEND EMAILS FOR WITHDRAWN GENES
        if (gene.getAssignmentStatus().equals(GeneStatus.WITHDRAWN)) {
            errMessages.add("TEMPORARY RULE: Skipping gene " + gene.toString() + " because its assignment status is Withdrawn");
            return null;
        }

        return gene;
    }
}