package org.mousephenotype.ri.core.entities;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class StatusImitsStatus {
    private int pk;
    private int statusPk;
    private int imitsStatusPk;
    private Date updatedAt;

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

    public int getImitsStatusPk() {
        return imitsStatusPk;
    }

    public void setImitsStatusPk(int imitsStatusPk) {
        this.imitsStatusPk = imitsStatusPk;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}