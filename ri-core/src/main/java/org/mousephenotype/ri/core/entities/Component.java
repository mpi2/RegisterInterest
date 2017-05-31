package org.mousephenotype.ri.core.entities;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 *
 * This class contains the possible components of interest (e.g. 'gene', 'phenotype', 'disease')
 */
public class Component {
    private int pk;
    private String name;
    private Date updatedAt;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
