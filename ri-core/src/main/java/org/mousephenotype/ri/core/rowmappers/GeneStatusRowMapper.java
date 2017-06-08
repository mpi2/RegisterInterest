package org.mousephenotype.ri.core.rowmappers;

import org.mousephenotype.ri.core.entities.GeneStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class GeneStatusRowMapper implements RowMapper<GeneStatus> {

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
    public GeneStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
        GeneStatus geneStatus = new GeneStatus();

        geneStatus.setPk(rs.getInt("pk"));

        geneStatus.setStatus(rs.getString("geneStatus"));
        int active = rs.getInt("active");
        geneStatus.setActive(active > 0 ? true : false);

        geneStatus.setCreatedAt(new Date(rs.getTimestamp("created_at").getTime()));
        geneStatus.setUpdatedAt(new Date(rs.getTimestamp("updated_at").getTime()));

        return geneStatus;
    }
}