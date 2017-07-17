package org.mousephenotype.ri.core.rowmappers;

import org.springframework.jdbc.core.RowMapper;

import org.mousephenotype.ri.core.entities.Gene;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class GeneRowMapper implements RowMapper<Gene> {

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
    public Gene mapRow(ResultSet rs, int rowNum) throws SQLException {
        Gene gene = new Gene();

        gene.setPk(rs.getInt("pk"));

        gene.setMgiAccessionId(rs.getString("mgi_accession_id"));
        gene.setSymbol(rs.getString("symbol"));
        gene.setAssignedTo(rs.getString("assigned_to"));
        gene.setAssignmentStatus(rs.getString("assignment_status"));
        Timestamp ts = rs.getTimestamp("assignment_status_date");
        gene.setAssignmentStatusDate(ts == null ? null : new Date(ts.getTime()));
        Integer i = rs.getInt("assignment_status_pk");
        gene.setAssignmentStatusPk((i == null) || (i == 0) ? null : i);

        gene.setConditionalAlleleProductionCentre(rs.getString("conditional_allele_production_centre"));
        gene.setConditionalAlleleProductionStatus(rs.getString("conditional_allele_production_status"));
        ts = rs.getTimestamp("conditional_allele_production_start_date");
        gene.setConditionalAlleleProductionStartDate(ts == null ? null : new Date(ts.getTime()));
        ts = rs.getTimestamp("conditional_allele_production_completed_date");
        gene.setConditionalAlleleProductionCompletedDate(ts == null ? null : new Date(ts.getTime()));
        i = rs.getInt("conditional_allele_production_status_pk");
        gene.setConditionalAlleleProductionStatusPk((i == null) || (i == 0) ? null : i);

        gene.setNullAlleleProductionCentre(rs.getString("null_allele_production_centre"));
        gene.setNullAlleleProductionStatus(rs.getString("null_allele_production_status"));
        ts = rs.getTimestamp("null_allele_production_start_date");
        gene.setNullAlleleProductionStartDate(ts == null ? null : new Date(ts.getTime()));
        ts = rs.getTimestamp("null_allele_production_completed_date");
        gene.setNullAlleleProductionCompletedDate(ts == null ? null : new Date(ts.getTime()));
        i = rs.getInt("null_allele_production_status_pk");
        gene.setNullAlleleProductionStatusPk((i == null) || (i == 0) ? null : i);

        gene.setPhenotypingCentre(rs.getString("phenotyping_centre"));
        gene.setPhenotypingStatus(rs.getString("phenotyping_status"));
        ts = rs.getTimestamp("phenotyping_status_date");
        gene.setPhenotypingStatusDate(ts == null ? null : new Date(ts.getTime()));
        i = rs.getInt("phenotyping_status_pk");
        gene.setPhenotypingStatusPk((i == null) || (i == 0) ? null : i);

        gene.setNumberOfSignificantPhenotypes(rs.getInt("number_of_significant_phenotypes"));

        gene.setCreatedAt(new Date(rs.getDate("created_at").getTime()));
        gene.setUpdatedAt(new Date(rs.getDate("updated_at").getTime()));

        return gene;
    }
}