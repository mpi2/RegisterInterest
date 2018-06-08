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

package org.mousephenotype.ri.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class Gene {

    private int pk;

    private String mgiAccessionId;
    private String symbol;

    @JsonIgnore
    private String assignedTo;

    @JsonIgnore
    private String assignmentStatus;

    @JsonIgnore
    private Date assignmentStatusDate;

    @JsonIgnore
    private String assignmentStatusDateString;

    @JsonIgnore
    private Integer assignmentStatusPk;

    private String riAssignmentStatus;

    @JsonIgnore
    private String conditionalAlleleProductionCentre;

    @JsonIgnore
    private String conditionalAlleleProductionStatus;

    @JsonIgnore
    private Date conditionalAlleleProductionStatusDate;

    @JsonIgnore
    private String conditionalAlleleProductionStatusDateString;

    @JsonIgnore
    private Date conditionalAlleleProductionStartDate;

    @JsonIgnore
    private String conditionalAlleleProductionStartDateString;

    @JsonIgnore
    private Integer conditionalAlleleProductionStatusPk;

    private String riConditionalAlleleProductionStatus;


    @JsonIgnore
    private String nullAlleleProductionCentre;

    @JsonIgnore
    private String nullAlleleProductionStatus;

    @JsonIgnore
    private Date nullAlleleProductionStatusDate;

    @JsonIgnore
    private String nullAlleleProductionStatusDateString;

    @JsonIgnore
    private Date nullAlleleProductionStartDate;

    @JsonIgnore
    private String nullAlleleProductionStartDateString;

    @JsonIgnore
    private Integer nullAlleleProductionStatusPk;

    private String riNullAlleleProductionStatus;


    @JsonIgnore
    private String phenotypingCentre;

    @JsonIgnore
    private String phenotypingStatus;

    @JsonIgnore
    private Date phenotypingStatusDate;

    @JsonIgnore
    private String phenotypingStatusDateString;

    @JsonIgnore
    private Integer phenotypingStatusPk;

    private String riPhenotypingStatus;

    @JsonIgnore
    private Integer numberOfSignificantPhenotypes;

    @JsonIgnore
    private String numberOfSignificantPhenotypesString;

    private Date createdAt;

    private Date updatedAt;


    // GETTERS AND SETTERS


    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getMgiAccessionId() {
        return mgiAccessionId;
    }

    public void setMgiAccessionId(String mgiAccessionId) {
        this.mgiAccessionId = mgiAccessionId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAssignmentStatus() {
        return assignmentStatus;
    }

    public void setAssignmentStatus(String assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public Date getAssignmentStatusDate() {
        return assignmentStatusDate;
    }

    public void setAssignmentStatusDate(Date assignmentStatusDate) {
        this.assignmentStatusDate = assignmentStatusDate;
    }

    public String getAssignmentStatusDateString() {
        return assignmentStatusDateString;
    }

    public void setAssignmentStatusDateString(String assignmentStatusDateString) {
        this.assignmentStatusDateString = assignmentStatusDateString;
    }

    public Integer getAssignmentStatusPk() {
        return assignmentStatusPk;
    }

    public void setAssignmentStatusPk(Integer assignmentStatusPk) {
        this.assignmentStatusPk = assignmentStatusPk;
    }

    public String getConditionalAlleleProductionCentre() {
        return conditionalAlleleProductionCentre;
    }

    public void setConditionalAlleleProductionCentre(String conditionalAlleleProductionCentre) {
        this.conditionalAlleleProductionCentre = conditionalAlleleProductionCentre;
    }

    public String getConditionalAlleleProductionStatus() {
        return conditionalAlleleProductionStatus;
    }

    public void setConditionalAlleleProductionStatus(String conditionalAlleleProductionStatus) {
        this.conditionalAlleleProductionStatus = conditionalAlleleProductionStatus;
    }

    public Date getConditionalAlleleProductionStatusDate() {
        return conditionalAlleleProductionStatusDate;
    }

    public void setConditionalAlleleProductionStatusDate(Date conditionalAlleleProductionStatusDate) {
        this.conditionalAlleleProductionStatusDate = conditionalAlleleProductionStatusDate;
    }

    public String getConditionalAlleleProductionStatusDateString() {
        return conditionalAlleleProductionStatusDateString;
    }

    public void setConditionalAlleleProductionStatusDateString(String conditionalAlleleProductionStatusDateString) {
        this.conditionalAlleleProductionStatusDateString = conditionalAlleleProductionStatusDateString;
    }

    public Date getConditionalAlleleProductionStartDate() {
        return conditionalAlleleProductionStartDate;
    }

    public void setConditionalAlleleProductionStartDate(Date conditionalAlleleProductionStartDate) {
        this.conditionalAlleleProductionStartDate = conditionalAlleleProductionStartDate;
    }

    public String getConditionalAlleleProductionStartDateString() {
        return conditionalAlleleProductionStartDateString;
    }

    public void setConditionalAlleleProductionStartDateString(String conditionalAlleleProductionStartDateString) {
        this.conditionalAlleleProductionStartDateString = conditionalAlleleProductionStartDateString;
    }

    public Integer getConditionalAlleleProductionStatusPk() {
        return conditionalAlleleProductionStatusPk;
    }

    public void setConditionalAlleleProductionStatusPk(Integer conditionalAlleleProductionStatusPk) {
        this.conditionalAlleleProductionStatusPk = conditionalAlleleProductionStatusPk;
    }

    public String getNullAlleleProductionCentre() {
        return nullAlleleProductionCentre;
    }

    public void setNullAlleleProductionCentre(String nullAlleleProductionCentre) {
        this.nullAlleleProductionCentre = nullAlleleProductionCentre;
    }

    public String getNullAlleleProductionStatus() {
        return nullAlleleProductionStatus;
    }

    public void setNullAlleleProductionStatus(String nullAlleleProductionStatus) {
        this.nullAlleleProductionStatus = nullAlleleProductionStatus;
    }

    public Date getNullAlleleProductionStatusDate() {
        return nullAlleleProductionStatusDate;
    }

    public void setNullAlleleProductionStatusDate(Date nullAlleleProductionStatusDate) {
        this.nullAlleleProductionStatusDate = nullAlleleProductionStatusDate;
    }

    public String getNullAlleleProductionStatusDateString() {
        return nullAlleleProductionStatusDateString;
    }

    public void setNullAlleleProductionStatusDateString(String nullAlleleProductionStatusDateString) {
        this.nullAlleleProductionStatusDateString = nullAlleleProductionStatusDateString;
    }

    public Date getNullAlleleProductionStartDate() {
        return nullAlleleProductionStartDate;
    }

    public void setNullAlleleProductionStartDate(Date nullAlleleProductionStartDate) {
        this.nullAlleleProductionStartDate = nullAlleleProductionStartDate;
    }

    public String getNullAlleleProductionStartDateString() {
        return nullAlleleProductionStartDateString;
    }

    public void setNullAlleleProductionStartDateString(String nullAlleleProductionStartDateString) {
        this.nullAlleleProductionStartDateString = nullAlleleProductionStartDateString;
    }

    public Integer getNullAlleleProductionStatusPk() {
        return nullAlleleProductionStatusPk;
    }

    public void setNullAlleleProductionStatusPk(Integer nullAlleleProductionStatusPk) {
        this.nullAlleleProductionStatusPk = nullAlleleProductionStatusPk;
    }

    public String getPhenotypingCentre() {
        return phenotypingCentre;
    }

    public void setPhenotypingCentre(String phenotypingCentre) {
        this.phenotypingCentre = phenotypingCentre;
    }

    public String getPhenotypingStatus() {
        return phenotypingStatus;
    }

    public void setPhenotypingStatus(String phenotypingStatus) {
        this.phenotypingStatus = phenotypingStatus;
    }

    public Date getPhenotypingStatusDate() {
        return phenotypingStatusDate;
    }

    public void setPhenotypingStatusDate(Date phenotypingStatusDate) {
        this.phenotypingStatusDate = phenotypingStatusDate;
    }

    public String getPhenotypingStatusDateString() {
        return phenotypingStatusDateString;
    }

    public void setPhenotypingStatusDateString(String phenotypingStatusDateString) {
        this.phenotypingStatusDateString = phenotypingStatusDateString;
    }

    public Integer getPhenotypingStatusPk() {
        return phenotypingStatusPk;
    }

    public void setPhenotypingStatusPk(Integer phenotypingStatusPk) {
        this.phenotypingStatusPk = phenotypingStatusPk;
    }

    public Integer getNumberOfSignificantPhenotypes() {
        return numberOfSignificantPhenotypes;
    }

    public void setNumberOfSignificantPhenotypes(Integer numberOfSignificantPhenotypes) {
        this.numberOfSignificantPhenotypes = numberOfSignificantPhenotypes;
    }

    public String getNumberOfSignificantPhenotypesString() {
        return numberOfSignificantPhenotypesString;
    }

    public void setNumberOfSignificantPhenotypesString(String numberOfSignificantPhenotypesString) {
        this.numberOfSignificantPhenotypesString = numberOfSignificantPhenotypesString;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }


    public String getRiAssignmentStatus() {
        return riAssignmentStatus;
    }

    public void setRiAssignmentStatus(String riAssignmentStatus) {
        this.riAssignmentStatus = riAssignmentStatus;
    }

    public String getRiConditionalAlleleProductionStatus() {
        return riConditionalAlleleProductionStatus;
    }

    public void setRiConditionalAlleleProductionStatus(String riConditionalAlleleProductionStatus) {
        this.riConditionalAlleleProductionStatus = riConditionalAlleleProductionStatus;
    }

    public String getRiNullAlleleProductionStatus() {
        return riNullAlleleProductionStatus;
    }

    public void setRiNullAlleleProductionStatus(String riNullAlleleProductionStatus) {
        this.riNullAlleleProductionStatus = riNullAlleleProductionStatus;
    }

    public String getRiPhenotypingStatus() {
        return riPhenotypingStatus;
    }

    public void setRiPhenotypingStatus(String riPhenotypingStatus) {
        this.riPhenotypingStatus = riPhenotypingStatus;
    }


    @Override
    public String toString() {
        return "Gene{" +
                "pk=" + pk +
                ", mgiAccessionId='" + mgiAccessionId + '\'' +
                ", symbol='" + symbol + '\'' +
                ", assignedTo='" + assignedTo + '\'' +
                ", assignmentStatus='" + assignmentStatus + '\'' +
                ", assignmentStatusDate=" + assignmentStatusDate +
                ", assignmentStatusPk=" + assignmentStatusPk +
                ", riAssignmentStatus='" + riAssignmentStatus + '\'' +
                ", conditionalAlleleProductionCentre='" + conditionalAlleleProductionCentre + '\'' +
                ", conditionalAlleleProductionStatus='" + conditionalAlleleProductionStatus + '\'' +
                ", conditionalAlleleProductionStatusPk=" + conditionalAlleleProductionStatusPk +
                ", riConditionalAlleleProductionStatus='" + riConditionalAlleleProductionStatus + '\'' +
                ", conditionalAlleleProductionStatusDate=" + conditionalAlleleProductionStatusDate +
                ", conditionalAlleleProductionStartDate=" + conditionalAlleleProductionStartDate +
                ", nullAlleleProductionCentre='" + nullAlleleProductionCentre + '\'' +
                ", nullAlleleProductionStatus='" + nullAlleleProductionStatus + '\'' +
                ", nullAlleleProductionStatusPk=" + nullAlleleProductionStatusPk +
                ", riNullAlleleProductionStatus='" + riNullAlleleProductionStatus + '\'' +
                ", nullAlleleProductionStatusDate=" + nullAlleleProductionStatusDate +
                ", nullAlleleProductionStartDate=" + nullAlleleProductionStartDate +
                ", phenotypingCentre='" + phenotypingCentre + '\'' +
                ", phenotypingStatus='" + phenotypingStatus + '\'' +
                ", phenotypingStatusDate=" + phenotypingStatusDate +
                ", phenotypingStatusPk=" + phenotypingStatusPk +
                ", riPhenotypingStatus='" + riPhenotypingStatus + '\'' +
                ", numberOfSignificantPhenotypes=" + numberOfSignificantPhenotypes +
                ", created_at=" + createdAt +
                ", updated_at=" + updatedAt +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gene gene = (Gene) o;

        if (!mgiAccessionId.equals(gene.mgiAccessionId)) return false;
        if (symbol != null ? !symbol.equals(gene.symbol) : gene.symbol != null) return false;
        if (assignedTo != null ? !assignedTo.equals(gene.assignedTo) : gene.assignedTo != null) return false;
        if (assignmentStatus != null ? !assignmentStatus.equals(gene.assignmentStatus) : gene.assignmentStatus != null)
            return false;
        if (assignmentStatusDate != null ? !assignmentStatusDate.equals(gene.assignmentStatusDate) : gene.assignmentStatusDate != null)
            return false;
        if (assignmentStatusPk != null ? !assignmentStatusPk.equals(gene.assignmentStatusPk) : gene.assignmentStatusPk != null)
            return false;
        if (conditionalAlleleProductionCentre != null ? !conditionalAlleleProductionCentre.equals(gene.conditionalAlleleProductionCentre) : gene.conditionalAlleleProductionCentre != null)
            return false;
        if (conditionalAlleleProductionStatus != null ? !conditionalAlleleProductionStatus.equals(gene.conditionalAlleleProductionStatus) : gene.conditionalAlleleProductionStatus != null)
            return false;
        if (conditionalAlleleProductionStatusPk != null ? !conditionalAlleleProductionStatusPk.equals(gene.conditionalAlleleProductionStatusPk) : gene.conditionalAlleleProductionStatusPk != null)
            return false;
        if (conditionalAlleleProductionStatusDate != null ? !conditionalAlleleProductionStatusDate.equals(gene.conditionalAlleleProductionStatusDate) : gene.conditionalAlleleProductionStatusDate != null)
            return false;
        if (conditionalAlleleProductionStartDate != null ? !conditionalAlleleProductionStartDate.equals(gene.conditionalAlleleProductionStartDate) : gene.conditionalAlleleProductionStartDate != null)
            return false;
        if (nullAlleleProductionCentre != null ? !nullAlleleProductionCentre.equals(gene.nullAlleleProductionCentre) : gene.nullAlleleProductionCentre != null)
            return false;
        if (nullAlleleProductionStatus != null ? !nullAlleleProductionStatus.equals(gene.nullAlleleProductionStatus) : gene.nullAlleleProductionStatus != null)
            return false;
        if (nullAlleleProductionStatusPk != null ? !nullAlleleProductionStatusPk.equals(gene.nullAlleleProductionStatusPk) : gene.nullAlleleProductionStatusPk != null)
            return false;
        if (nullAlleleProductionStatusDate != null ? !nullAlleleProductionStatusDate.equals(gene.nullAlleleProductionStatusDate) : gene.nullAlleleProductionStatusDate != null)
            return false;
        if (nullAlleleProductionStartDate != null ? !nullAlleleProductionStartDate.equals(gene.nullAlleleProductionStartDate) : gene.nullAlleleProductionStartDate != null)
            return false;
        if (phenotypingCentre != null ? !phenotypingCentre.equals(gene.phenotypingCentre) : gene.phenotypingCentre != null)
            return false;
        if (phenotypingStatus != null ? !phenotypingStatus.equals(gene.phenotypingStatus) : gene.phenotypingStatus != null)
            return false;
        if (phenotypingStatusDate != null ? !phenotypingStatusDate.equals(gene.phenotypingStatusDate) : gene.phenotypingStatusDate != null)
            return false;
        if (phenotypingStatusPk != null ? !phenotypingStatusPk.equals(gene.phenotypingStatusPk) : gene.phenotypingStatusPk != null)
            return false;
        if (numberOfSignificantPhenotypes != null ? !numberOfSignificantPhenotypes.equals(gene.numberOfSignificantPhenotypes) : gene.numberOfSignificantPhenotypes != null)
            return false;

        return true;
    }


    @Override
    public int hashCode() {
        int result = mgiAccessionId.hashCode();
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (assignedTo != null ? assignedTo.hashCode() : 0);
        result = 31 * result + (assignmentStatus != null ? assignmentStatus.hashCode() : 0);
        result = 31 * result + (assignmentStatusDate != null ? assignmentStatusDate.hashCode() : 0);
        result = 31 * result + (assignmentStatusPk != null ? assignmentStatusPk.hashCode() : 0);
        result = 31 * result + (conditionalAlleleProductionCentre != null ? conditionalAlleleProductionCentre.hashCode() : 0);
        result = 31 * result + (conditionalAlleleProductionStatus != null ? conditionalAlleleProductionStatus.hashCode() : 0);
        result = 31 * result + (conditionalAlleleProductionStatusPk != null ? conditionalAlleleProductionStatusPk.hashCode() : 0);
        result = 31 * result + (conditionalAlleleProductionStatusDate != null ? conditionalAlleleProductionStatusDate.hashCode() : 0);
        result = 31 * result + (conditionalAlleleProductionStartDate != null ? conditionalAlleleProductionStartDate.hashCode() : 0);
        result = 31 * result + (nullAlleleProductionCentre != null ? nullAlleleProductionCentre.hashCode() : 0);
        result = 31 * result + (nullAlleleProductionStatus != null ? nullAlleleProductionStatus.hashCode() : 0);
        result = 31 * result + (nullAlleleProductionStatusPk != null ? nullAlleleProductionStatusPk.hashCode() : 0);
        result = 31 * result + (nullAlleleProductionStatusDate != null ? nullAlleleProductionStatusDate.hashCode() : 0);
        result = 31 * result + (nullAlleleProductionStartDate != null ? nullAlleleProductionStartDate.hashCode() : 0);
        result = 31 * result + (phenotypingCentre != null ? phenotypingCentre.hashCode() : 0);
        result = 31 * result + (phenotypingStatus != null ? phenotypingStatus.hashCode() : 0);
        result = 31 * result + (phenotypingStatusDate != null ? phenotypingStatusDate.hashCode() : 0);
        result = 31 * result + (phenotypingStatusPk != null ? phenotypingStatusPk.hashCode() : 0);
        result = 31 * result + (numberOfSignificantPhenotypes != null ? numberOfSignificantPhenotypes.hashCode() : 0);
        return result;
    }
}