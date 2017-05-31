package org.mousephenotype.ri.core.entities;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class ContactGene {
    private int pk;
    private int contactPk;
    private int genePk;
    private Date updatedAt;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getContactPk() {
        return contactPk;
    }

    public void setContactPk(int contactPk) {
        this.contactPk = contactPk;
    }

    public int getGenePk() {
        return genePk;
    }

    public void setGenePk(int genePk) {
        this.genePk = genePk;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}