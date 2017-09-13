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
public class GeneSent {
    private int pk;

    private String subject;
    private String body;

    private int geneContactPk;
    private Integer assignmentStatusPk;
    private Integer conditionalAlleleProductionStatusPk;
    private Integer nullAlleleProductionStatusPk;
    private Integer phenotypingStatusPk;

    private Date createdAt;
    private Date SentAt;
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

    public int getGeneContactPk() {
        return geneContactPk;
    }

    public void setGeneContactPk(int geneContactPk) {
        this.geneContactPk = geneContactPk;
    }

    public Integer getAssignmentStatusPk() {
        return assignmentStatusPk;
    }

    public void setAssignmentStatusPk(Integer assignmentStatusPk) {
        this.assignmentStatusPk = assignmentStatusPk;
    }

    public Integer getConditionalAlleleProductionStatusPk() {
        return conditionalAlleleProductionStatusPk;
    }

    public void setConditionalAlleleProductionStatusPk(Integer conditionalAlleleProductionStatusPk) {
        this.conditionalAlleleProductionStatusPk = conditionalAlleleProductionStatusPk;
    }

    public Integer getNullAlleleProductionStatusPk() {
        return nullAlleleProductionStatusPk;
    }

    public void setNullAlleleProductionStatusPk(Integer nullAlleleProductionStatusPk) {
        this.nullAlleleProductionStatusPk = nullAlleleProductionStatusPk;
    }

    public Integer getPhenotypingStatusPk() {
        return phenotypingStatusPk;
    }

    public void setPhenotypingStatusPk(Integer phenotypingStatusPk) {
        this.phenotypingStatusPk = phenotypingStatusPk;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getSentAt() {
        return SentAt;
    }

    public void setSentAt(Date sentAt) {
        SentAt = sentAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}