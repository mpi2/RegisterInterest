/*******************************************************************************
 *  Copyright © 2017 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.ri.core.entities;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class ImitsStatus {
    private int pk;
    private Integer geneStatusPk;          // This field is not part of the imits_status table, but it is useful to have a place to put the equivalent register interest status pk.
    private String status;
    private Date createdAt;
    private Date updatedAt;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public Integer getGeneStatusPk() {
        return geneStatusPk;
    }

    public void setGeneStatusPk(Integer geneStatusPk) {
        this.geneStatusPk = geneStatusPk;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "ImitsStatus{" +
                "pk=" + pk +
                ", geneStatusPk=" + geneStatusPk +
                ", status='" + status + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
