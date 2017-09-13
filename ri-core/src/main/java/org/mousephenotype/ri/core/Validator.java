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

        if (gene.getAssignmentStatus() != null) {
            // if assignment_status == not_planned (NP)

            if (gene.getAssignmentStatus().equals(GeneStatus.NOT_PLANNED)) {
                if ((gene.getConditionalAlleleProductionStatusPk() != null) ||
                        (gene.getNullAlleleProductionStatusPk() != null) ||
                        (gene.getPhenotypingStatusPk() != null) ||
                        (gene.getNumberOfSignificantPhenotypes() > 0)) {
                    errMessages.add("Data Error for " + gene.getMgiAccessionId() + ": assignmentStatus = Not Planned and other statuses/counts are not empty");
                    return null;
                }
            }

            // if assignment_status == Withdrawn (W)
            if (gene.getAssignmentStatus().equals(GeneStatus.WITHDRAWN)) {
                if ((gene.getConditionalAlleleProductionStatusPk() != null) ||
                        (gene.getNullAlleleProductionStatusPk() != null) ||
                        (gene.getPhenotypingStatusPk() != null) ||
                        (gene.getNumberOfSignificantPhenotypes() > 0)) {
                    errMessages.add("Data Error for " + gene.getMgiAccessionId() + ": assignmentStatus = Withdrawn and other statuses/counts are not empty");
                    return null;
                }
            }

            // if assignment_status == Production And Phenotyping Planned (PAPP) AND phenotyping_status NOT empty AND NOT MOUSE_PRODUCED
            if ((gene.getAssignmentStatus().equals(GeneStatus.PRODUCTION_AND_PHENOTYPING_PLANNED)) &&
                    (gene.getPhenotypingStatusPk() != null)) {

                if (((gene.getConditionalAlleleProductionStatusPk() != null) && (gene.getConditionalAlleleProductionStatus().equals(GeneStatus.MOUSE_PRODUCED))) ||
                        ((gene.getNullAlleleProductionStatusPk() != null) && (gene.getNullAlleleProductionStatus().equals(GeneStatus.MOUSE_PRODUCED)))) {
                    // NULL statement
                } else {
                    errMessages.add("Data Error for " + gene.getMgiAccessionId() + ": assignmentStatus = PAPP and production status != Mouse Produced");
                    return null;
                }
            }

            // mrelac (2017-07-07)
            // TEMPORARY RULE: DO NOT SEND EMAILS FOR WITHDRAWN GENES
            if (gene.getAssignmentStatus().equals(GeneStatus.WITHDRAWN)) {
                errMessages.add("TEMPORARY RULE: Skipping gene " + gene.toString() + " because its assignment status is Withdrawn");
                return null;
            }
        }

        return gene;
    }
}