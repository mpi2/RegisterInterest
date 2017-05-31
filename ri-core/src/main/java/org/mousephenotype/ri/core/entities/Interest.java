package org.mousephenotype.ri.core.entities;

import java.util.Date;

/**
 * Created by mrelac on 13/04/2017.
 *
 * This is an amalgam of gene and contact
 */
public class Interest {
    private Integer contactPk;
    private String address;
    private boolean isActive;
    private Integer genePk;
    private String mgiAccessionId;
    private Date updatedAt;

    public Interest() {

    }

    public Interest(String address, String mgiAccessionId) {
        this.address = address;
        this.mgiAccessionId = mgiAccessionId;
    }

    public Integer getContactPk() {
        return contactPk;
    }

    public void setContactPk(Integer contactPk) {
        this.contactPk = contactPk;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getGenePk() {
        return genePk;
    }

    public void setGenePk(Integer genePk) {
        this.genePk = genePk;
    }

    public String getMgiAccessionId() {
        return mgiAccessionId;
    }

    public void setMgiAccessionId(String mgiAccessionId) {
        this.mgiAccessionId = mgiAccessionId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Interest{" +
                "address='" + address + '\'' +
                ", mgiAccessionId='" + mgiAccessionId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interest interest = (Interest) o;



        if (address != null ? !address.equals(interest.address) : interest.address != null) return false;
        return mgiAccessionId != null ? mgiAccessionId.equals(interest.mgiAccessionId) : interest.mgiAccessionId == null;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (mgiAccessionId != null ? mgiAccessionId.hashCode() : 0);

        return result;
    }
}