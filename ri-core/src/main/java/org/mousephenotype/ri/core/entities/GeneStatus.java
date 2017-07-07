package org.mousephenotype.ri.core.entities;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class GeneStatus {
    private int pk;
    private int genePk;
    private String status;
    private boolean active;
    private Date createdAt;
    private Date updatedAt;

    public static final String MORE_PHENOTYPING_DATA_AVAILABLE      = "more_phenotyping_data_available";
    public static final String MOUSE_PRODUCED                       = "mouse_produced";
    public static final String MOUSE_PRODUCTION_STARTED             = "mouse_production_started";
    public static final String NOT_PLANNED                          = "not_planned";
    public static final String PHENOTYPING_DATA_AVAILABLE           = "phenotyping_data_available";
    public static final String PRODUCTION_AND_PHENOTYPING_PLANNED   = "production_and_phenotyping_planned";
    public static final String REGISTER                             = "register";
    public static final String UNREGISTER                           = "unregister";
    public static final String WITHDRAWN                            = "withdrawn";

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getGenePk() {
        return genePk;
    }

    public void setGenePk(int genePk) {
        this.genePk = genePk;
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
}
