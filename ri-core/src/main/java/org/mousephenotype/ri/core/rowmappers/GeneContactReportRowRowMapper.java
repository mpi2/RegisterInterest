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

package org.mousephenotype.ri.core.rowmappers;

import org.mousephenotype.ri.core.entities.GeneContactReportRow;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by mrelac on 13/03/2018.
 */
public class GeneContactReportRowRowMapper implements RowMapper<GeneContactReportRow> {

    /**
     * Implementations must implement this method to map each row of data
     * in the ResultSet. This method should not call {@code next()} on
     * the ResultSet; it is only supposed to map values of the current row.
     *
     * @param rs     the ResultSet to map (pre-initialized for the current row)
     * @param rowNum the number of the current row
     * @return the result object for the current row
     * @throws SQLException if a SQLException is encountered getting
     *                      column values (that is, there's no need to catch SQLException)
     */
    @Override
    public GeneContactReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        GeneContactReportRow geneContactReportRow = new GeneContactReportRow();

        geneContactReportRow.setContactEmail(rs.getString("contact_email"));
        geneContactReportRow.setContactActiveState(rs.getInt("contact_active_state") == 1 ? 1 : 0);
        geneContactReportRow.setContactCreatedAt(new Date(rs.getTimestamp("contact_created_at").getTime()));
        geneContactReportRow.setMarkerSymbol(rs.getString("marker_symbol"));
        geneContactReportRow.setMgiAccessionId(rs.getString("mgi_accession_id"));
        geneContactReportRow.setGeneInterestCreatedDate(new Date(rs.getTimestamp("gene_interest_created_at").getTime()));

        return geneContactReportRow;
    }
}