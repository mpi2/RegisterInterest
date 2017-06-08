package org.mousephenotype.ri.core.entities;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class ImitsStatus {
    private int pk;
    private Integer geneStatusPk;          // This field is not part of the imits_status table, but it is useful to have a place to put the equivalent register interest status pk.
    private String status;
    private boolean active;
    private Date createdAt;
    private Date updatedAt;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public Integer getGeneStatusPk() {
        return geneStatusPk;
    }

    public void setGeneStatusPk(Integer geneStatusPk) {
        this.geneStatusPk = geneStatusPk;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
        return "ImitsStatus{" +
                "pk=" + pk +
                ", geneStatusPk=" + geneStatusPk +
                ", status='" + status + '\'' +
                ", active=" + active +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
