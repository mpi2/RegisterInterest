package org.mousephenotype.ri.core.entities;

/**
 * Created by mrelac on 22/05/2017.
 */
public class GeneStatusChange {

    private int pk;
    private String geneMgiAccessionId;
    private String geneMarkerSymbol;
    private String geneAssignmentStatus;
    private String geneAssignedTo;
    private String geneAssignmentStatusDate;

    private String conditionalAlleleProductionStatus;
    private String xconditionalAlleleProductionCentre;
    private String conditionalAlleleStatusDate;

    private String nullAlleleProductionStatus;
    private String nullAlleleProductionCentre;
    private String nullAlleleStatusDate;

    private String phenotypingStatus;
    private String phenotypingCentre;
    private String phenotypingStatusDate;

    private String numberOfSignificantPhenotypes;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getGeneMgiAccessionId() {
        return geneMgiAccessionId;
    }

    public void setGeneMgiAccessionId(String geneMgiAccessionId) {
        this.geneMgiAccessionId = geneMgiAccessionId;
    }

    public String getGeneMarkerSymbol() {
        return geneMarkerSymbol;
    }

    public void setGeneMarkerSymbol(String geneMarkerSymbol) {
        this.geneMarkerSymbol = geneMarkerSymbol;
    }

    public String getGeneAssignmentStatus() {
        return geneAssignmentStatus;
    }

    public void setGeneAssignmentStatus(String geneAssignmentStatus) {
        this.geneAssignmentStatus = geneAssignmentStatus;
    }

    public String getGeneAssignedTo() {
        return geneAssignedTo;
    }

    public void setGeneAssignedTo(String geneAssignedTo) {
        this.geneAssignedTo = geneAssignedTo;
    }

    public String getGeneAssignmentStatusDate() {
        return geneAssignmentStatusDate;
    }

    public void setGeneAssignmentStatusDate(String geneAssignmentStatusDate) {
        this.geneAssignmentStatusDate = geneAssignmentStatusDate;
    }

    public String getConditionalAlleleProductionStatus() {
        return conditionalAlleleProductionStatus;
    }

    public void setConditionalAlleleProductionStatus(String conditionalAlleleProductionStatus) {
        this.conditionalAlleleProductionStatus = conditionalAlleleProductionStatus;
    }

    public String getConditionalAlleleProductionCentre() {
        return xconditionalAlleleProductionCentre;
    }

    public void setConditionalAlleleProductionCentre(String conditionalAlleleProductionCentre) {
        this.xconditionalAlleleProductionCentre = conditionalAlleleProductionCentre;
    }

    public String getConditionalAlleleStatusDate() {
        return conditionalAlleleStatusDate;
    }

    public void setConditionalAlleleStatusDate(String conditionalAlleleStatusDate) {
        this.conditionalAlleleStatusDate = conditionalAlleleStatusDate;
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

    public String getNullAlleleStatusDate() {
        return nullAlleleStatusDate;
    }

    public void setNullAlleleStatusDate(String nullAlleleStatusDate) {
        this.nullAlleleStatusDate = nullAlleleStatusDate;
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

    public String getPhenotypingStatusDate() {
        return phenotypingStatusDate;
    }

    public void setPhenotypingStatusDate(String phenotypingStatusDate) {
        this.phenotypingStatusDate = phenotypingStatusDate;
    }

    public String getNumberOfSignificantPhenotypes() {
        return numberOfSignificantPhenotypes;
    }

    public void setNumberOfSignificantPhenotypes(String numberOfSignificantPhenotypes) {
        this.numberOfSignificantPhenotypes = numberOfSignificantPhenotypes;
    }

    @Override
    public String toString() {
        return "GeneStatusChange{" +
                "pk=" + pk +
                ", geneMgiAccessionId='" + geneMgiAccessionId + '\'' +
                ", geneMarkerSymbol='" + geneMarkerSymbol + '\'' +
                ", geneAssignmentStatus='" + geneAssignmentStatus + '\'' +
                ", geneAssignedTo='" + geneAssignedTo + '\'' +
                ", geneAssignmentStatusDate='" + geneAssignmentStatusDate + '\'' +
                ", conditionalAlleleProductionStatus='" + conditionalAlleleProductionStatus + '\'' +
                ", condtionalAlleleProductionCentre='" + xconditionalAlleleProductionCentre + '\'' +
                ", conditionalAlleleStatusDate='" + conditionalAlleleStatusDate + '\'' +
                ", nullAlleleProductionStatus='" + nullAlleleProductionStatus + '\'' +
                ", nullAlleleProductionCentre='" + nullAlleleProductionCentre + '\'' +
                ", nullAlleleStatusDate='" + nullAlleleStatusDate + '\'' +
                ", phenotypingStatus='" + phenotypingStatus + '\'' +
                ", phenotypingCentre='" + phenotypingCentre + '\'' +
                ", phenotypingStatusDate='" + phenotypingStatusDate + '\'' +
                ", numberOfSignificantPhenotypes='" + numberOfSignificantPhenotypes + '\'' +
                '}';
    }
}