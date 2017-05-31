package org.mousephenotype.ri.core.entities;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class Sent {
    private int pk;
    private int componentPk;
    private int contactPk;
    private int genePk;
    private int statusPk;
    private String subject;
    private String body;
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

    public int getGenePk() {
        return genePk;
    }

    public void setGenePk(int genePk) {
        this.genePk = genePk;
    }

    public int getStatusPk() {
        return statusPk;
    }

    public void setStatusPk(int statusPk) {
        this.statusPk = statusPk;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}