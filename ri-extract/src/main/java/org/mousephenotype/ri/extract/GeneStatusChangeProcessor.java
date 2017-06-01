/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.ri.extract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import org.mousephenotype.ri.core.entities.GeneStatusChange;
import org.mousephenotype.ri.core.entities.ImitsStatus;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.mousephenotype.ri.core.ParseUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 09/06/16.
 */
public class GeneStatusChangeProcessor implements ItemProcessor<GeneStatusChange, GeneStatusChange> {

    private int geneStatusChangeCount = 0;
    private Map<String, ImitsStatus> imitsStatusMap;
    private int lineNumber = 0;

    private ParseUtils parseUtils = new ParseUtils();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public final  Set<String> errMessages = ConcurrentHashMap.newKeySet();       // This is the java 8 way to create a concurrent hash set.


    private final String[] expectedHeadings = new String[]{
              "gene_mgi_accession_id"
            , "gene_marker_symbol"
            , "gene_assignment_status"
            , "gene_assigned_to"
            , "gene_assignment_status_date"
            , "conditional_allele_production_status"
            , "conditional_allele_production_centre"
            , "conditional_allele_status_date"
            , "null_allele_production_status"
            , "null_allele_production_centre"
            , "null_allele_status_date"
            , "phenotyping_status"
            , "phenotyping_centre"
            , "phenotyping_status_date"
            , "number_of_significant_phenotypes"
    };


    public GeneStatusChangeProcessor(Map<String, ImitsStatus> imitsStatusMap) {
        this.imitsStatusMap = imitsStatusMap;
    }


    @Override
    public GeneStatusChange process(GeneStatusChange geneStatusChange) throws Exception {

        lineNumber++;

        // Validate the file using the heading names and initialize any collections.
        if (lineNumber == 1) {
            String[] actualHeadings = new String[] {
                    geneStatusChange.getMgiAccessionId()
                  , geneStatusChange.getSymbol()
                  , geneStatusChange.getAssignmentStatus()
                  , geneStatusChange.getAssignedTo()
                  , geneStatusChange.getAssignmentStatusDateString()

                  , geneStatusChange.getConditionalAlleleProductionStatus()
                  , geneStatusChange.getConditionalAlleleProductionCentre()
                  , geneStatusChange.getConditionalAlleleStatusDateString()

                  , geneStatusChange.getNullAlleleProductionStatus()
                  , geneStatusChange.getNullAlleleProductionCentre()
                  , geneStatusChange.getNullAlleleStatusDateString()

                  , geneStatusChange.getPhenotypingStatus()
                  , geneStatusChange.getPhenotypingCentre()
                  , geneStatusChange.getPhenotypingStatusDateString()

                  , geneStatusChange.getNumberOfSignificantPhenotypesString()
            };

            for (int i = 0; i < expectedHeadings.length; i++) {
                if ( ! expectedHeadings[i].equals(actualHeadings[i])) {
                    throw new InterestException("Expected heading '" + expectedHeadings[i] + "' but found '" + actualHeadings[i] + "'.");
                }
            }

            return null;
        }

        // Validate fields. If errors, log them and return null.

        // status
        if ( ! imitsStatusMap.containsKey(geneStatusChange.getAssignmentStatus())) {
            errMessages.add("Unknown gene assignment status '" + geneStatusChange.getAssignmentStatus() + +'"');
            return null;
        }

        if ((geneStatusChange.getConditionalAlleleProductionStatus() != null) && ( ! geneStatusChange.getConditionalAlleleProductionStatus().trim().isEmpty())) {
            if ( ! imitsStatusMap.containsKey(geneStatusChange.getConditionalAlleleProductionStatus())) {
                errMessages.add("Unknown conditional allele production status '" + geneStatusChange.getConditionalAlleleProductionStatus() + "'");
                return null;
            }
        }
        if ((geneStatusChange.getNullAlleleProductionStatus() != null) && ( ! geneStatusChange.getNullAlleleProductionStatus().trim().isEmpty())) {
            if (!imitsStatusMap.containsKey(geneStatusChange.getNullAlleleProductionStatus())) {
                errMessages.add("Unknown null allele production status '" + geneStatusChange.getNullAlleleProductionStatus() + "'");
                return null;
            }
        }
        if ((geneStatusChange.getPhenotypingStatus() != null) && ( ! geneStatusChange.getPhenotypingStatus().trim().isEmpty())) {
            if (!imitsStatusMap.containsKey(geneStatusChange.getPhenotypingStatus())) {
                errMessages.add("Unknown phenotyping status '" + geneStatusChange.getPhenotypingStatus() + "'");
                return null;
            }
        }

        // date
        SimpleDateFormat sdf = new SimpleDateFormat("y-M-d h:m:s");
        if ((geneStatusChange.getAssignmentStatusDateString() != null) && ( ! geneStatusChange.getAssignmentStatusDateString().trim().isEmpty())) {
            Date date = parseUtils.tryParseDate(sdf, geneStatusChange.getAssignmentStatusDateString());
            if (date == null) {
                errMessages.add("Invalid date '" + geneStatusChange.getAssignmentStatusDate() + "'");
                return null;
            }
            geneStatusChange.setAssignmentStatusDate(date);
        }
        if ((geneStatusChange.getConditionalAlleleStatusDateString() != null) && ( ! geneStatusChange.getConditionalAlleleStatusDateString().trim().isEmpty())) {
            Date date = parseUtils.tryParseDate(sdf, geneStatusChange.getConditionalAlleleStatusDateString());
            if (date == null) {
                errMessages.add("Invalid date '" + geneStatusChange.getConditionalAlleleStatusDate() + "'");
                return null;
            }
            geneStatusChange.setConditionalAlleleStatusDate(date);
        }
        if ((geneStatusChange.getNullAlleleStatusDateString() != null) && ( ! geneStatusChange.getNullAlleleStatusDateString().trim().isEmpty())) {
            Date date = parseUtils.tryParseDate(sdf, geneStatusChange.getNullAlleleStatusDateString());
            if (date == null) {
                errMessages.add("Invalid date '" + geneStatusChange.getNullAlleleStatusDate() + "'");
                return null;
            }
            geneStatusChange.setNullAlleleStatusDate(date);
        }
        if ((geneStatusChange.getPhenotypingStatusDateString() != null) && ( ! geneStatusChange.getPhenotypingStatusDateString().trim().isEmpty())) {
            Date date = parseUtils.tryParseDate(sdf, geneStatusChange.getPhenotypingStatusDateString());
            if (date == null) {
                errMessages.add("Invalid date '" + geneStatusChange.getPhenotypingStatusDate() + "'");
                return null;
            }
            geneStatusChange.setPhenotypingStatusDate(date);
        }

        // number
        Integer number = parseUtils.tryParseInt(geneStatusChange.getNumberOfSignificantPhenotypesString());
        if (number == null) {
            errMessages.add("Invalid number '" + geneStatusChange.getNumberOfSignificantPhenotypes() + "'");
            return null;
        }
        geneStatusChange.setNumberOfSignificantPhenotypes(number);

        geneStatusChangeCount++;

        return geneStatusChange;
    }

    public int getGeneStatusChangeCount() {
        return geneStatusChangeCount;
    }


    public Set<String> getErrMessages() {
        return errMessages;
    }
}