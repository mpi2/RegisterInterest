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

import org.mousephenotype.ri.core.entities.Gene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

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
public class GeneProcessor implements ItemProcessor<Gene, Gene> {

    private int geneCount = 0;
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


    public GeneProcessor(Map<String, ImitsStatus> imitsStatusMap) {
        this.imitsStatusMap = imitsStatusMap;
    }


    @Override
    public Gene process(Gene gene) throws Exception {

        lineNumber++;

        // Validate the file using the heading names and initialize any collections.
        if (lineNumber == 1) {
            String[] actualHeadings = new String[] {
                    gene.getMgiAccessionId()
                  , gene.getSymbol()
                  , gene.getAssignmentStatus()
                  , gene.getAssignedTo()
                  , gene.getAssignmentStatusDateString()

                  , gene.getConditionalAlleleProductionStatus()
                  , gene.getConditionalAlleleProductionCentre()
                  , gene.getConditionalAlleleProductionStatusDateString()

                  , gene.getNullAlleleProductionStatus()
                  , gene.getNullAlleleProductionCentre()
                  , gene.getNullAlleleProductionStatusDateString()

                  , gene.getPhenotypingStatus()
                  , gene.getPhenotypingCentre()
                  , gene.getPhenotypingStatusDateString()

                  , gene.getNumberOfSignificantPhenotypesString()
            };

            for (int i = 0; i < expectedHeadings.length; i++) {
                if ( ! expectedHeadings[i].equals(actualHeadings[i])) {
                    throw new InterestException("Expected heading '" + expectedHeadings[i] + "' but found '" + actualHeadings[i] + "'.");
                }
            }

            return null;
        }

        // Validate fields. If errors, log them and return null.

        // statuses
        if ((gene.getAssignmentStatus() != null) && ( ! gene.getAssignmentStatus().trim().isEmpty())) {
            if (!imitsStatusMap.containsKey(gene.getAssignmentStatus())) {
                errMessages.add("Unknown gene assignment status '" + gene.getAssignmentStatus() + +'"');
                return null;
            }
        }


        if ((gene.getConditionalAlleleProductionStatus() != null) && ( ! gene.getConditionalAlleleProductionStatus().trim().isEmpty())) {
            if ( ! imitsStatusMap.containsKey(gene.getConditionalAlleleProductionStatus())) {
                errMessages.add("Unknown conditional allele production status '" + gene.getConditionalAlleleProductionStatus() + "'");
                return null;
            }
        }
        if ((gene.getNullAlleleProductionStatus() != null) && ( ! gene.getNullAlleleProductionStatus().trim().isEmpty())) {
            if ( ! imitsStatusMap.containsKey(gene.getNullAlleleProductionStatus())) {
                errMessages.add("Unknown null allele production status '" + gene.getNullAlleleProductionStatus() + "'");
                return null;
            }
        }
        if ((gene.getPhenotypingStatus() != null) && ( ! gene.getPhenotypingStatus().trim().isEmpty())) {
            if (!imitsStatusMap.containsKey(gene.getPhenotypingStatus())) {
                errMessages.add("Unknown phenotyping status '" + gene.getPhenotypingStatus() + "'");
                return null;
            }
        }

        // date
        SimpleDateFormat sdf = new SimpleDateFormat("y-M-d h:m:s");
        if ((gene.getAssignmentStatusDateString() != null) && ( ! gene.getAssignmentStatusDateString().trim().isEmpty())) {
            Date date = parseUtils.tryParseDate(sdf, gene.getAssignmentStatusDateString());
            if (date == null) {
                errMessages.add("Invalid date '" + gene.getAssignmentStatusDate() + "'");
                return null;
            }
            gene.setAssignmentStatusDate(date);
        }
        if ((gene.getConditionalAlleleProductionStatusDateString() != null) && ( ! gene.getConditionalAlleleProductionStatusDateString().trim().isEmpty())) {
            Date date = parseUtils.tryParseDate(sdf, gene.getConditionalAlleleProductionStatusDateString());
            if (date == null) {
                errMessages.add("Invalid date '" + gene.getConditionalAlleleProductionStatusDate() + "'");
                return null;
            }
            gene.setConditionalAlleleProductionStatusDate(date);
        }
        if ((gene.getNullAlleleProductionStatusDateString() != null) && ( ! gene.getNullAlleleProductionStatusDateString().trim().isEmpty())) {
            Date date = parseUtils.tryParseDate(sdf, gene.getNullAlleleProductionStatusDateString());
            if (date == null) {
                errMessages.add("Invalid date '" + gene.getNullAlleleProductionStatusDate() + "'");
                return null;
            }
            gene.setNullAlleleProductionStatusDate(date);
        }
        if ((gene.getPhenotypingStatusDateString() != null) && ( ! gene.getPhenotypingStatusDateString().trim().isEmpty())) {
            Date date = parseUtils.tryParseDate(sdf, gene.getPhenotypingStatusDateString());
            if (date == null) {
                errMessages.add("Invalid date '" + gene.getPhenotypingStatusDate() + "'");
                return null;
            }
            gene.setPhenotypingStatusDate(date);
        }

        // number
        Integer number = parseUtils.tryParseInt(gene.getNumberOfSignificantPhenotypesString());
        if (number == null) {
            errMessages.add("Invalid number '" + gene.getNumberOfSignificantPhenotypes() + "'");
            return null;
        }
        gene.setNumberOfSignificantPhenotypes(number);

        // Populate the ri status fields based on imits status string.
        if ((gene.getAssignmentStatus() != null) && ( ! gene.getAssignmentStatus().trim().isEmpty())) {
            gene.setAssignmentStatusPk(imitsStatusMap.get(gene.getAssignmentStatus()).getStatus_pk());
        } else {
            gene.setAssignmentStatusPk(null);
        }
        if ((gene.getConditionalAlleleProductionStatus() != null) && ( ! gene.getConditionalAlleleProductionStatus().trim().isEmpty())) {
            gene.setConditionalAlleleProductionStatusPk(imitsStatusMap.get(gene.getConditionalAlleleProductionStatus()).getStatus_pk());
        } else {
            gene.setConditionalAlleleProductionStatusPk(null);
        }
        if ((gene.getNullAlleleProductionStatus() != null) && ( ! gene.getNullAlleleProductionStatus().trim().isEmpty())) {
            gene.setNullAlleleProductionStatusPk(imitsStatusMap.get(gene.getNullAlleleProductionStatus()).getStatus_pk());
        } else {
            gene.setNullAlleleProductionStatusPk(null);
        }
        if ((gene.getPhenotypingStatus() != null) && ( ! gene.getPhenotypingStatus().trim().isEmpty())) {
            gene.setPhenotypingStatusPk(imitsStatusMap.get(gene.getPhenotypingStatus()).getStatus_pk());
        } else {
            gene.setPhenotypingStatusPk(null);
        }

        geneCount++;

        return gene;
    }

    public int getGeneCount() {
        return geneCount;
    }


    public Set<String> getErrMessages() {
        return errMessages;
    }
}