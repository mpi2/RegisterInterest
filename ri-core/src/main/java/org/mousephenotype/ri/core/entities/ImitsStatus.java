package org.mousephenotype.ri.core.entities;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class ImitsStatus {
    private int pk;
    private String status;
    private boolean active;
    private Date updatedAt;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
