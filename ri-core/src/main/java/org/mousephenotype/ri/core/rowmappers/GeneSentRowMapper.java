package org.mousephenotype.ri.core.rowmappers;

import org.springframework.jdbc.core.RowMapper;

import org.mousephenotype.ri.core.entities.GeneSent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class GeneSentRowMapper implements RowMapper<GeneSent> {

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

        Integer i = rs.getInt("assignment_status_pk");
        geneSent.setAssignmentStatusPk((i == null) || (i == 0) ? null : i);

        i = rs.getInt("conditional_allele_production_status_pk");
        geneSent.setConditionalAlleleProductionStatusPk((i == null) || (i == 0) ? null : i);

        i = rs.getInt("null_allele_production_status_pk");
        geneSent.setNullAlleleProductionStatusPk((i == null) || (i == 0) ? null : i);

                i = rs.getInt("phenotyping_status_pk");
        geneSent.setPhenotypingStatusPk((i == null) || (i == 0) ? null : i);

        geneSent.setCreatedAt(new Date(rs.getTimestamp("created_at").getTime()));

        Timestamp ts = rs.getTimestamp("sent_at");
        geneSent.setSentAt(ts == null ? null : new Date(ts.getTime()));

        geneSent.setUpdatedAt(new Date(rs.getTimestamp("updated_at").getTime()));

        return geneSent;
    }
}