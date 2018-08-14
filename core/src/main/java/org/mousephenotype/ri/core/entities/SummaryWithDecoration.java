/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.ri.core.entities;

import java.util.List;

/**
 * This class mimics the {@link Summary} class, replacing the {@link List<Gene>} with a {@link List<GeneWithDecoration>}
 * that indicates whether or not the gene status element should be decorated to show that the gene status has changed.
 */
public class SummaryWithDecoration extends Summary {

    private String emailAddress;
    private List<Gene> genes;


    public SummaryWithDecoration(Summary summary) {
        this.emailAddress = summary.getEmailAddress();
        this.genes = summary.getGenes();
    }

    public boolean isDecorated() {
        if ((genes == null) || (genes.isEmpty())) {
            return false;
        }

        for (Gene gene : genes) {
            if (gene instanceof GeneWithDecoration) {
                if (((GeneWithDecoration) gene).isDecorated()) {
                    return true;
                }
            }
        }

        return false;
    }


    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<Gene> getGenes() {
        return genes;
    }


    @Override
    public String toString() {
        return "Summary{" +
                "emailAddress='" + emailAddress + '\'' +
                ", genes=" + genes +
                '}';
    }
}