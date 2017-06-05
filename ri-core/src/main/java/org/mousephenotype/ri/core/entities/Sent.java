package org.mousephenotype.ri.core.entities;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class Sent {
    private int pk;

    private String subject;
    private String body;
    private int componentPk;
    private int contactGenePk;
    private int statusPk;
    private Date updatedAt;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
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

    public int getComponentPk() {
        return componentPk;
    }

    public void setComponentPk(int componentPk) {
        this.componentPk = componentPk;
    }

    public int getContactGenePk() {
        return contactGenePk;
    }

    public void setContactGenePk(int contactGenePk) {
        this.contactGenePk = contactGenePk;
    }

    public int getStatusPk() {
        return statusPk;
    }

    public void setStatusPk(int statusPk) {
        this.statusPk = statusPk;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}