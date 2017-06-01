package org.mousephenotype.ri.core.entities;

import java.util.Date;

/**
 * Created by mrelac on 22/05/2017.
 */
public class GeneStatusChange {

    private int pk;

    private int statusPk;
    private String mgiAccessionId;
    private String symbol;
    private String assignmentStatus;
    private String assignedTo;
    private Date assignmentStatusDate;
    private String assignmentStatusDateString;

    private String conditionalAlleleProductionStatus;
    private String conditionalAlleleProductionCentre;
    private Date conditionalAlleleStatusDate;
    private String conditionalAlleleStatusDateString;

    private String nullAlleleProductionStatus;
    private String nullAlleleProductionCentre;
    private Date nullAlleleStatusDate;
    private String nullAlleleStatusDateString;

    private String phenotypingStatus;
    private String phenotypingCentre;
    private Date phenotypingStatusDate;
    private String phenotypingStatusDateString;

    private Integer numberOfSignificantPhenotypes;
    private String numberOfSignificantPhenotypesString;

    private Date updated_at;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getStatusPk() {
        return statusPk;
    }

    public void setStatusPk(int statusPk) {
        this.statusPk = statusPk;
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

    public String getAssignmentStatus() {
        return assignmentStatus;
    }

    public void setAssignmentStatus(String assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
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

    public String getConditionalAlleleProductionStatus() {
        return conditionalAlleleProductionStatus;
    }

    public void setConditionalAlleleProductionStatus(String conditionalAlleleProductionStatus) {
        this.conditionalAlleleProductionStatus = conditionalAlleleProductionStatus;
    }

    public String getConditionalAlleleProductionCentre() {
        return conditionalAlleleProductionCentre;
    }

    public void setConditionalAlleleProductionCentre(String conditionalAlleleProductionCentre) {
        this.conditionalAlleleProductionCentre = conditionalAlleleProductionCentre;
    }

    public Date getConditionalAlleleStatusDate() {
        return conditionalAlleleStatusDate;
    }

    public void setConditionalAlleleStatusDate(Date conditionalAlleleStatusDate) {
        this.conditionalAlleleStatusDate = conditionalAlleleStatusDate;
    }

    public String getConditionalAlleleStatusDateString() {
        return conditionalAlleleStatusDateString;
    }

    public void setConditionalAlleleStatusDateString(String conditionalAlleleStatusDateString) {
        this.conditionalAlleleStatusDateString = conditionalAlleleStatusDateString;
    }

    public String getNullAlleleProductionStatus() {
        return nullAlleleProductionStatus;
    }

    public void setNullAlleleProductionStatus(String nullAlleleProductionStatus) {
        this.nullAlleleProductionStatus = nullAlleleProductionStatus;
    }

    public String getNullAlleleProductionCentre() {
        return nullAlleleProductionCentre;
    }

    public void setNullAlleleProductionCentre(String nullAlleleProductionCentre) {
        this.nullAlleleProductionCentre = nullAlleleProductionCentre;
    }

    public Date getNullAlleleStatusDate() {
        return nullAlleleStatusDate;
    }

    public void setNullAlleleStatusDate(Date nullAlleleStatusDate) {
        this.nullAlleleStatusDate = nullAlleleStatusDate;
    }

    public String getNullAlleleStatusDateString() {
        return nullAlleleStatusDateString;
    }

    public void setNullAlleleStatusDateString(String nullAlleleStatusDateString) {
        this.nullAlleleStatusDateString = nullAlleleStatusDateString;
    }

    public String getPhenotypingStatus() {
        return phenotypingStatus;
    }

    public void setPhenotypingStatus(String phenotypingStatus) {
        this.phenotypingStatus = phenotypingStatus;
    }

    public String getPhenotypingCentre() {
        return phenotypingCentre;
    }

    public void setPhenotypingCentre(String phenotypingCentre) {
        this.phenotypingCentre = phenotypingCentre;
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

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "GeneStatusChange{" +
                "pk=" + pk +
                ", statusPk=" + statusPk +
                ", mgiAccessionId='" + mgiAccessionId + '\'' +
                ", symbol='" + symbol + '\'' +
                ", assignmentStatus='" + assignmentStatus + '\'' +
                ", assignedTo='" + assignedTo + '\'' +
                ", assignmentStatusDate=" + assignmentStatusDate +
                ", conditionalAlleleProductionStatus='" + conditionalAlleleProductionStatus + '\'' +
                ", conditionalAlleleProductionCentre='" + conditionalAlleleProductionCentre + '\'' +
                ", conditionalAlleleStatusDate=" + conditionalAlleleStatusDate +
                ", nullAlleleProductionStatus='" + nullAlleleProductionStatus + '\'' +
                ", nullAlleleProductionCentre='" + nullAlleleProductionCentre + '\'' +
                ", nullAlleleStatusDate=" + nullAlleleStatusDate +
                ", phenotypingStatus='" + phenotypingStatus + '\'' +
                ", phenotypingCentre='" + phenotypingCentre + '\'' +
                ", phenotypingStatusDate=" + phenotypingStatusDate +
                ", numberOfSignificantPhenotypes=" + numberOfSignificantPhenotypes +
                ", updated_at=" + updated_at +
                '}';
    }
}