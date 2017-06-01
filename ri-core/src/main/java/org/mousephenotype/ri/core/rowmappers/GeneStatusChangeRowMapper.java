package org.mousephenotype.ri.core.rowmappers;

import org.mousephenotype.ri.core.entities.GeneStatusChange;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class GeneStatusChangeRowMapper implements RowMapper<GeneStatusChange> {

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
    public GeneStatusChange mapRow(ResultSet rs, int rowNum) throws SQLException {
        GeneStatusChange gene = new GeneStatusChange();

        gene.setPk(rs.getInt("pk"));

        gene.setStatusPk(rs.getInt("status_pk"));

        gene.setMgiAccessionId(rs.getString("mgi_accession_id"));
        gene.setAssignmentStatus(rs.getString("gene_assignment_status"));
        gene.setAssignedTo(rs.getString("gene_assigned_to"));
        gene.setAssignmentStatusDate(rs.getDate("gene_assignment_status_date"));
        
        gene.setConditionalAlleleProductionStatus(rs.getString("conditional_allele_production_status"));
        gene.setConditionalAlleleProductionCentre(rs.getString("conditional_allele_production_centre"));
        gene.setConditionalAlleleStatusDate(rs.getDate("conditional_allele_status_date"));

        gene.setNullAlleleProductionStatus(rs.getString("null_allele_production_status"));
        gene.setNullAlleleProductionCentre(rs.getString("null_allele_production_centre"));
        gene.setNullAlleleStatusDate(rs.getDate("null_allele_status_date"));
        
        gene.setPhenotypingStatus(rs.getString("phenotyping_status"));
        gene.setPhenotypingCentre(rs.getString("phenotyping_centre"));
        gene.setPhenotypingStatusDate(rs.getDate("phenotyping_status_date"));

        gene.setNumberOfSignificantPhenotypes(rs.getInt("number_of_significant_phenotypes"));

        gene.setUpdated_at(rs.getDate("updated_at"));

        return gene;
    }
}