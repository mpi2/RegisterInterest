package org.mousephenotype.ri.core.rowmappers;

import org.springframework.jdbc.core.RowMapper;

import org.mousephenotype.ri.core.entities.GeneSent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class SentRowMapper implements RowMapper<GeneSent> {

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
    public GeneSent mapRow(ResultSet rs, int rowNum) throws SQLException {
        GeneSent geneSent = new GeneSent();

        geneSent.setPk(rs.getInt("pk"));

        geneSent.setSubject((rs.getString("subject")));
        geneSent.setBody((rs.getString("body")));

        geneSent.setGeneContactPk(rs.getInt("gene_contact_pk"));
        geneSent.setAssignmentStatusPk(rs.getInt("assignment_status_pk"));
        geneSent.setConditionalAlleleProductionStatusPk(rs.getInt("conditional_allele_production_status_pk"));
        geneSent.setNullAlleleProductionStatusPk(rs.getInt("null_allele_production_status_pk"));
        geneSent.setPhenotypingStatusPk(rs.getInt("phenotyping_status_pk"));

        geneSent.setCreatedAt(new Date(rs.getTimestamp("created_at").getTime()));
        geneSent.setSentAt(new Date(rs.getTimestamp("sent_at").getTime()));
        geneSent.setUpdatedAt(new Date(rs.getTimestamp("updated_at").getTime()));

        return geneSent;
    }
}