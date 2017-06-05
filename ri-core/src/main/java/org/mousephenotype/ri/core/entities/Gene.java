package org.mousephenotype.ri.core.entities;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class Gene {

    private int pk;

    private String mgiAccessionId;
    private String symbol;
    private String assignedTo;
    private String assignmentStatus;
    private Date assignmentStatusDate;
    private String assignmentStatusDateString;
    private Integer assignmentStatusPk;

    private String conditionalAlleleProductionCentre;
    private String conditionalAlleleProductionStatus;
    private Date conditionalAlleleProductionStatusDate;
    private String conditionalAlleleProductionStatusDateString;
    private Integer conditionalAlleleProductionStatusPk;

    private String nullAlleleProductionCentre;
    private String nullAlleleProductionStatus;
    private Date nullAlleleProductionStatusDate;
    private String nullAlleleProductionStatusDateString;
    private Integer nullAlleleProductionStatusPk;

    private String phenotypingCentre;
    private String phenotypingStatus;
    private Date phenotypingStatusDate;
    private String phenotypingStatusDateString;
    private Integer phenotypingStatusPk;

    private Integer numberOfSignificantPhenotypes;
    private String numberOfSignificantPhenotypesString;

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
                ", conditionalAlleleProductionStatusDate=" + conditionalAlleleProductionStatusDate +
                ", conditionalAlleleProductionStatusPk=" + conditionalAlleleProductionStatusPk +
                ", nullAlleleProductionCentre='" + nullAlleleProductionCentre + '\'' +
                ", nullAlleleProductionStatus='" + nullAlleleProductionStatus + '\'' +
                ", nullAlleleProductionStatusDate=" + nullAlleleProductionStatusDate +
                ", nullAlleleProductionStatusPk=" + nullAlleleProductionStatusPk +
                ", phenotypingCentre='" + phenotypingCentre + '\'' +
                ", phenotypingStatus='" + phenotypingStatus + '\'' +
                ", phenotypingStatusDate=" + phenotypingStatusDate +
                ", phenotypingStatusPk=" + phenotypingStatusPk +
                ", numberOfSignificantPhenotypes=" + numberOfSignificantPhenotypes +
                ", updated_at=" + updatedAt +
                '}';
    }
}