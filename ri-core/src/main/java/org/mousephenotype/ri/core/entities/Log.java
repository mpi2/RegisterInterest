package org.mousephenotype.ri.core.entities;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class Log {
    private int pk;
    private int componentPk;
    private int contactPk;
    private int imitsStatusPk;
    private int sentPk;
    private int statusPk;
    private String message;
    private Date updatedAt;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getComponentPk() {
        return componentPk;
    }

    public void setComponentPk(int componentPk) {
        this.componentPk = componentPk;
    }

    public int getContactPk() {
        return contactPk;
    }

    public void setContactPk(int contactPk) {
        this.contactPk = contactPk;
    }

    public int getImitsStatusPk() {
        return imitsStatusPk;
    }

    public void setImitsStatusPk(int imitsStatusPk) {
        this.imitsStatusPk = imitsStatusPk;
    }

    public int getSentPk() {
        return sentPk;
    }

    public void setSentPk(int sentPk) {
        this.sentPk = sentPk;
    }

    public int getStatusPk() {
        return statusPk;
    }

    public void setStatusPk(int statusPk) {
        this.statusPk = statusPk;
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