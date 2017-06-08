package org.mousephenotype.ri.core.entities;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class Log {
    private int pk;

    private int contactPk;
    private int geneStatusPk;
    private int imitsStatusPk;
    private int genePk;
    private int sentPk;

    private String message;
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

    public int getGeneStatusPk() {
        return geneStatusPk;
    }

    public void setGeneStatusPk(int geneStatusPk) {
        this.geneStatusPk = geneStatusPk;
    }

    public int getImitsStatusPk() {
        return imitsStatusPk;
    }

    public void setImitsStatusPk(int imitsStatusPk) {
        this.imitsStatusPk = imitsStatusPk;
    }

    public int getGenePk() {
        return genePk;
    }

    public void setGenePk(int genePk) {
        this.genePk = genePk;
    }

    public int getSentPk() {
        return sentPk;
    }

    public void setSentPk(int sentPk) {
        this.sentPk = sentPk;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}