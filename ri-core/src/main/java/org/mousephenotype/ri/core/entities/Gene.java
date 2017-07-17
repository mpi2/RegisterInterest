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

    @JsonIgnore
    private String conditionalAlleleProductionCentre;

    @JsonIgnore
    private String conditionalAlleleProductionStatus;

    @JsonIgnore
    private Date conditionalAlleleProductionStartDate;

    @JsonIgnore
    private String conditionalAlleleProductionStartDateString;

    @JsonIgnore
    private Date conditionalAlleleProductionCompletedDate;

    @JsonIgnore
    private String conditionalAlleleProductionCompletedDateString;

    @JsonIgnore
    private Integer conditionalAlleleProductionStatusPk;

    @JsonIgnore
    private String nullAlleleProductionCentre;

    @JsonIgnore
    private String nullAlleleProductionStatus;

    @JsonIgnore
    private Date nullAlleleProductionStartDate;

    @JsonIgnore
    private String nullAlleleProductionStartDateString;

    @JsonIgnore
    private Date nullAlleleProductionCompletedDate;

    @JsonIgnore
    private String nullAlleleProductionCompletedDateString;

    @JsonIgnore
    private Integer nullAlleleProductionStatusPk;

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

    @JsonIgnore
    private Integer numberOfSignificantPhenotypes;

    @JsonIgnore
    private String numberOfSignificantPhenotypesString;

    private Date createdAt;

    private Date updatedAt;

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

    public Date getConditionalAlleleProductionCompletedDate() {
        return conditionalAlleleProductionCompletedDate;
    }

    public void setConditionalAlleleProductionCompletedDate(Date conditionalAlleleProductionCompletedDate) {
        this.conditionalAlleleProductionCompletedDate = conditionalAlleleProductionCompletedDate;
    }

    public String getConditionalAlleleProductionCompletedDateString() {
        return conditionalAlleleProductionCompletedDateString;
    }

    public void setConditionalAlleleProductionCompletedDateString(String conditionalAlleleProductionCompletedDateString) {
        this.conditionalAlleleProductionCompletedDateString = conditionalAlleleProductionCompletedDateString;
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

    public Date getNullAlleleProductionCompletedDate() {
        return nullAlleleProductionCompletedDate;
    }

    public void setNullAlleleProductionCompletedDate(Date nullAlleleProductionCompletedDate) {
        this.nullAlleleProductionCompletedDate = nullAlleleProductionCompletedDate;
    }

    public String getNullAlleleProductionCompletedDateString() {
        return nullAlleleProductionCompletedDateString;
    }

    public void setNullAlleleProductionCompletedDateString(String nullAlleleProductionCompletedDateString) {
        this.nullAlleleProductionCompletedDateString = nullAlleleProductionCompletedDateString;
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
                ", conditionalAlleleProductionCentre='" + conditionalAlleleProductionCentre + '\'' +
                ", conditionalAlleleProductionStatus='" + conditionalAlleleProductionStatus + '\'' +
                ", conditionalAlleleProductionStartDate=" + conditionalAlleleProductionStartDate +
                ", conditionalAlleleProductionCompletedDate=" + conditionalAlleleProductionCompletedDate +
                ", conditionalAlleleProductionStatusPk=" + conditionalAlleleProductionStatusPk +
                ", nullAlleleProductionCentre='" + nullAlleleProductionCentre + '\'' +
                ", nullAlleleProductionStatus='" + nullAlleleProductionStatus + '\'' +
                ", nullAlleleProductionStartDate=" + nullAlleleProductionStartDate +
                ", nullAlleleProductionCompletedDate=" + nullAlleleProductionCompletedDate +
                ", nullAlleleProductionStatusPk=" + nullAlleleProductionStatusPk +
                ", phenotypingCentre='" + phenotypingCentre + '\'' +
                ", phenotypingStatus='" + phenotypingStatus + '\'' +
                ", phenotypingStatusDate=" + phenotypingStatusDate +
                ", phenotypingStatusPk=" + phenotypingStatusPk +
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
        if (conditionalAlleleProductionStartDate != null ? !conditionalAlleleProductionStartDate.equals(gene.conditionalAlleleProductionStartDate) : gene.conditionalAlleleProductionStartDate != null)
            return false;
        if (conditionalAlleleProductionCompletedDate != null ? !conditionalAlleleProductionCompletedDate.equals(gene.conditionalAlleleProductionCompletedDate) : gene.conditionalAlleleProductionCompletedDate != null)
            return false;
        if (conditionalAlleleProductionStatusPk != null ? !conditionalAlleleProductionStatusPk.equals(gene.conditionalAlleleProductionStatusPk) : gene.conditionalAlleleProductionStatusPk != null)
            return false;
        if (nullAlleleProductionCentre != null ? !nullAlleleProductionCentre.equals(gene.nullAlleleProductionCentre) : gene.nullAlleleProductionCentre != null)
            return false;
        if (nullAlleleProductionStatus != null ? !nullAlleleProductionStatus.equals(gene.nullAlleleProductionStatus) : gene.nullAlleleProductionStatus != null)
            return false;
        if (nullAlleleProductionStartDate != null ? !nullAlleleProductionStartDate.equals(gene.nullAlleleProductionStartDate) : gene.nullAlleleProductionStartDate != null)
            return false;
        if (nullAlleleProductionCompletedDate != null ? !nullAlleleProductionCompletedDate.equals(gene.nullAlleleProductionCompletedDate) : gene.nullAlleleProductionCompletedDate != null)
            return false;
        if (nullAlleleProductionStatusPk != null ? !nullAlleleProductionStatusPk.equals(gene.nullAlleleProductionStatusPk) : gene.nullAlleleProductionStatusPk != null)
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
        result = 31 * result + (conditionalAlleleProductionStartDate != null ? conditionalAlleleProductionStartDate.hashCode() : 0);
        result = 31 * result + (conditionalAlleleProductionCompletedDate != null ? conditionalAlleleProductionCompletedDate.hashCode() : 0);
        result = 31 * result + (conditionalAlleleProductionStatusPk != null ? conditionalAlleleProductionStatusPk.hashCode() : 0);
        result = 31 * result + (nullAlleleProductionCentre != null ? nullAlleleProductionCentre.hashCode() : 0);
        result = 31 * result + (nullAlleleProductionStatus != null ? nullAlleleProductionStatus.hashCode() : 0);
        result = 31 * result + (nullAlleleProductionStartDate != null ? nullAlleleProductionStartDate.hashCode() : 0);
        result = 31 * result + (nullAlleleProductionCompletedDate != null ? nullAlleleProductionCompletedDate.hashCode() : 0);
        result = 31 * result + (nullAlleleProductionStatusPk != null ? nullAlleleProductionStatusPk.hashCode() : 0);
        result = 31 * result + (phenotypingCentre != null ? phenotypingCentre.hashCode() : 0);
        result = 31 * result + (phenotypingStatus != null ? phenotypingStatus.hashCode() : 0);
        result = 31 * result + (phenotypingStatusDate != null ? phenotypingStatusDate.hashCode() : 0);
        result = 31 * result + (phenotypingStatusPk != null ? phenotypingStatusPk.hashCode() : 0);
        result = 31 * result + (numberOfSignificantPhenotypes != null ? numberOfSignificantPhenotypes.hashCode() : 0);
        return result;
    }
}