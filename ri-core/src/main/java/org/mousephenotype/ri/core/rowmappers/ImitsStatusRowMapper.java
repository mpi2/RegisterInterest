package org.mousephenotype.ri.core.rowmappers;

import org.springframework.jdbc.core.RowMapper;

import org.mousephenotype.ri.core.entities.ImitsStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class ImitsStatusRowMapper implements RowMapper<ImitsStatus> {

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
    public ImitsStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
        ImitsStatus imitsStatus = new ImitsStatus();

        imitsStatus.setPk(rs.getInt("pk"));
        imitsStatus.setStatus((rs.getString("status")));
        int active = rs.getInt("active");
        imitsStatus.setActive(active > 0 ? true : false);
        imitsStatus.setUpdatedAt(new Date(rs.getTimestamp("updated_at").getTime()));

        return imitsStatus;
    }
}