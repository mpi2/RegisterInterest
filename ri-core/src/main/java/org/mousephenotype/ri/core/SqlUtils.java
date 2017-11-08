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

package org.mousephenotype.ri.core;

import org.mousephenotype.ri.core.entities.*;
import org.mousephenotype.ri.core.entities.report.GenesOfInterestByPopularity;
import org.mousephenotype.ri.core.exceptions.InterestException;
import org.mousephenotype.ri.core.rowmappers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by mrelac on 12/05/2017.
 */
public class SqlUtils {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull
    private NamedParameterJdbcTemplate jdbcInterest;
    
    @Inject
    public SqlUtils(NamedParameterJdbcTemplate jdbcInterest) {
        this.jdbcInterest = jdbcInterest;
    }


    /**
     * Determine if a column exists in a table specific for a MySQL database
     *
     * @param connection the connection to use to query the database
     * @param tableName the table to
     * @param columnName the column in the table
     * @return true = column name exists in table, false = column missing from table
     * @throws SQLException on db access error
     */
    public Boolean columnInSchemaMysql(Connection connection, String tableName, String columnName) throws SQLException {

        Boolean found = Boolean.FALSE;
        String columnQuery = "SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=(SELECT database()) AND TABLE_NAME=? AND column_name=?";

        try (PreparedStatement p = connection.prepareStatement(columnQuery)) {
            p.setString(1, tableName);
            p.setString(2, columnName);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                found = Boolean.TRUE;
            }
        }

        return found;
    }

    public void createSpringBatchTables(DataSource datasource) {

        logger.info("Creating SPRING BATCH tables");
        org.springframework.core.io.Resource r = new ClassPathResource("org/springframework/batch/core/schema-mysql.sql");
        ResourceDatabasePopulator p = new ResourceDatabasePopulator(r);
        p.execute(datasource);
    }

    public Map<Integer, String> getEmailAddressesByGeneContactPk() {
        Map<Integer, String> results = new HashMap<>();
        final String query =
                "SELECT gc.pk, c.address\n" +
                "FROM contact c\n" +
                "JOIN gene_contact gc ON gc.contact_pk      = c. pk\n" +
                "JOIN gene_sent    gs ON gs.gene_contact_pk = gc.pk";

        List<Map<String, Object>> listMap = jdbcInterest.queryForList(query, new HashMap<String, Object>());
        for (Map<String, Object> map : listMap) {
            int pk = (Integer)map.get("pk");
            String address = map.get("address").toString();
            results.put(pk, address);
        }

        return results;
    }

    /**
     * Return the {@link Gene}
     *
     * @param mgiAccessionId the MGI accession id of the desired gene
     *
     * @return the {@link Gene} matching the mgiAccessionId if found; null otherwise
     */
    public Gene getGene(String mgiAccessionId) {

        final String query = "SELECT * FROM gene WHERE mgi_accession_id = :mgi_accession_id";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("mgi_accession_id", mgiAccessionId);

        List<Gene> genes = jdbcInterest.query(query, parameterMap, new GeneRowMapper());

        return (genes.isEmpty() ? null : genes.get(0));
    }

    /**
     * Return the {@link GeneSent}
     *
     * @param pk the gene_sent primary key
     *
     * @return the {@link GeneSent} matching the geneContactPk if found; null otherwise
     */
    public GeneSent getGeneSent(int pk) {

        final String query = "SELECT * FROM gene_sent WHERE pk = :pk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pk", pk);

        List<GeneSent> genesSent = jdbcInterest.query(query, parameterMap, new GeneSentRowMapper());

        return (genesSent.isEmpty() ? null : genesSent.get(0));
    }

    /**
     * @return A map of all genes, indexed by mgi accession id.
     */
    public Map<String, Gene> getGenes() {

        Map<String, Gene> genes = new HashMap<>();

        final String query = "SELECT * FROM gene";

        Map<String, Object> parameterMap = new HashMap<>();

        List<Gene> genesList = jdbcInterest.query(query, parameterMap, new GeneRowMapper());
        for (Gene gene : genesList) {
            genes.put(gene.getMgiAccessionId(), gene);
        }

        return genes;
    }

    /**
     * @return A map of all genes, indexed by primary key.
     */
    public Map<Integer, Gene> getGenesByPk() {

        Map<Integer, Gene> genes = new HashMap<>();

        final String query = "SELECT * FROM gene";

        Map<String, Object> parameterMap = new HashMap<>();

        List<Gene> genesList = jdbcInterest.query(query, parameterMap, new GeneRowMapper());
        for (Gene gene : genesList) {
            genes.put(gene.getPk(), gene);
        }

        return genes;
    }

    /**
     * @return A {@link List} of
     */
    public List<GenesOfInterestByPopularity> getGenesOfInterestByPopularity() {

        final String query =
                "SELECT\n" +
                "  g.mgi_accession_id,\n" +
                "  g.symbol,\n" +
                "  g.assigned_to,\n" +
                "  g.assignment_status,\n" +
                "  g.assignment_status_date,\n" +
                "  count(g.symbol) AS subscriber_count,\n" +
                "  GROUP_CONCAT(c.address, \"::\", gc.created_at ORDER BY gc.created_at SEPARATOR ' | ') AS subscribers\n" +
                "FROM gene g\n" +
                "JOIN gene_contact gc ON gc.gene_pk = g. pk\n" +
                "JOIN contact      c  ON c. pk      = gc.contact_pk\n" +
                "GROUP BY g.symbol\n" +
                "ORDER BY count(address) DESC, g.symbol";

        Map<String, Object> parameterMap = new HashMap<>();

        List<GenesOfInterestByPopularity> genes = jdbcInterest.query(query, parameterMap, new GenesOfInterestByPopularityRowMapper());

        return genes;
    }

    /**
     * @return a {@link Map} of all imits status, indexed by status (i.e. name)
     */
    public Map<String, ImitsStatus> getImitsStatusMap() {

        Map<String, ImitsStatus> imitsStatusMap = new HashMap<>();
        String query = "SELECT * FROM imits_status";

        Map<String, Object> parameterMap = new HashMap<>();

        List<ImitsStatus> imitsStatusList = jdbcInterest.query(query, parameterMap, new ImitsStatusRowMapper());
        for (ImitsStatus imitsStatus : imitsStatusList) {
            imitsStatusMap.put(imitsStatus.getStatus(), imitsStatus);
        }

        return imitsStatusMap;
    }

    /**
     * Return a list of {@link Interest} instances matching {@code email}, {@code type}, and {@code gene}
     *
     * @param email The desired contact's email address (may be null)
     * @param type The desired resource type (e.g. gene, phenotype, disease) (may be null)
     * @param gene The mgi accession id of the desired gene (may be null)
     *
     * @return a list of matching {@link Interest} instances
     */
    public List<Interest> getInterests(String email, String type, String gene) {

        List<Interest> interests = new ArrayList<>();

        StringBuilder queryContacts = new StringBuilder("SELECT * FROM contact");
        Map<String, Object> parameterMap = new HashMap<>();

        if (email != null) {
            queryContacts.append(" WHERE address = :email");
            parameterMap.put("email", email);
        }

        List<Contact> contacts = jdbcInterest.query(queryContacts.toString(), parameterMap, new ContactRowMapper());

        for (Contact contact : contacts) {
            List<Gene> genes;
            if ((type == null) || (type.equals("gene"))) {
                String queryGenes =
                        "SELECT * FROM gene g\n" +
                        "JOIN gene_contact gc ON gc.gene_pk = g.pk\n" +
                        "WHERE gc.contact_pk = :contact_pk AND gc.active = 1";
                parameterMap.put("contact_pk", contact.getPk());

                if (gene != null) {
                    queryGenes += "\n  AND g.mgi_accession_id = :mgi_accession_id";
                    parameterMap.put("mgi_accession_id", gene);
                }
                genes = jdbcInterest.query(queryGenes, parameterMap, new GeneRowMapper());
                if (genes.isEmpty()) {
                    continue;
                }
            } else {
                continue;
            }

            Interest interest = new Interest();
            interest.setContact(contact);
            interest.setGenes(genes);

            interests.add(interest);
        }

        return interests;
    }

    /**
     * Return the  {@link Contact}
     *
     * @param emailAddress the email address of the desired contact
     *
     * @return the {@link Contact} matching the emailAddress if found; null otherwise
     */
    public Contact getContact(String emailAddress) {

        final String query = "SELECT * FROM contact WHERE address = :emailAddress";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("emailAddress", emailAddress);

        List<Contact> contacts = jdbcInterest.query(query, parameterMap, new ContactRowMapper());

        return (contacts.isEmpty() ? null : contacts.get(0));
    }

    /**
     *
     * @return a {@link List} of all {@link GeneContact} entries
     */
    public List<GeneContact> getGeneContacts() {
        final String query = "SELECT * FROM gene_contact";

        Map<String, Object> parameterMap = new HashMap<>();

        return jdbcInterest.query(query, parameterMap, new GeneContactRowMapper());
    }


    /**
     *
     * @param gene The mgi accession id of the gene of interest
     * @param email the email address of interest
     * @return the {@link GeneContact} instance, if found; null otherwise
     */
    public GeneContact getGeneContact(String gene, String email) {
        String query =
                "SELECT * FROM gene_contact gc\n" +
                "JOIN gene g ON g.pk = gc.gene_pk\n" +
                "JOIN contact c ON c.pk = gc.contact_pk\n" +
                "WHERE g.mgi_accession_id = :gene AND c.address = :email";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("gene", gene);
        parameterMap.put("email", email);

        List<GeneContact> list = jdbcInterest.query(query, parameterMap, new GeneContactRowMapper());

        return (list.isEmpty() ? null : list.get(0));
    }

    /**
     *
     * @return A {@link Map} of all {@link GeneSent} instances, indexed by gene_contact_pk
     */
    public Map<Integer, GeneSent> getGeneSent() {

        Map<Integer, GeneSent> sentMap = new HashMap<>();

        final String query = "SELECT * FROM gene_sent";
        Map<String, Object> parameterMap = new HashMap<>();

        List<GeneSent> geneSentList = jdbcInterest.query(query, parameterMap, new GeneSentRowMapper());
        for (GeneSent geneSent : geneSentList) {
            sentMap.put(geneSent.getGeneContactPk(), geneSent);
        }

        return sentMap;
    }

    public List<GeneSent> getGenesScheduledForSending() {

        final String query = "SELECT * FROM gene_sent WHERE sent_at IS NULL";
        Map<String, Object> parameterMap = new HashMap<>();

        List<GeneSent> genesSentList = jdbcInterest.query(query, parameterMap, new GeneSentRowMapper());

        return genesSentList;
    }

    /**
     *
     * @return A {@link Map} of {@link GeneStatus} instances, keyed by status
     */
    public Map<String, GeneStatus> getStatusMap() {

        Map<String, GeneStatus> statusMap = new HashMap<>();

        final String query = "SELECT * FROM gene_status";
        Map<String, Object> parameterMap = new HashMap<>();

        List<GeneStatus> geneStatusList = jdbcInterest.query(query, parameterMap, new GeneStatusRowMapper());
        for (GeneStatus geneStatus : geneStatusList) {
            statusMap.put(geneStatus.getStatus(), geneStatus);
        }

        return statusMap;
    }

    /**
     *Insert or update {@link Gene} and {@link Contact} into the gene_contact table. Return the count of inserted rows.
     *
     * @param genePk The primary key gene to be inserted/updated
     * @param contactPk The primary key of the contact to be inserted/updated
     * @param active the {@link GeneContact} active flag (0 = inactive; 1 = active; 2 = pending unregister
     * @param createdAt The @link GeneContact} creation date. If NULL, the current time and date is used.
     *
     * @return the count of inserted/activated geneContacts.
     */
    public int insertOrUpdateGeneContact(int genePk, int contactPk, int active, Date createdAt) throws InterestException {
        int count = 0;
        String insert = "INSERT INTO gene_contact(contact_pk, gene_pk, created_at, active) " +
                        "VALUES (:contact_pk, :gene_pk, :created_at, 1)";
        String update = "UPDATE gene_contact SET active = :active WHERE contact_pk = :contact_pk AND gene_pk = :gene_pk";

        if (createdAt == null) {
            createdAt = new Date();
        }

        // Insert/activate gene_contact.
        Map<String, Object> parameterMap = new HashMap<>();
        try {
            parameterMap.put("contact_pk", contactPk);
            parameterMap.put("gene_pk", genePk);
            parameterMap.put("created_at", createdAt);
            parameterMap.put("active", active);

            count += jdbcInterest.update(insert, parameterMap);

        } catch (DuplicateKeyException e) {

            count += jdbcInterest.update(update, parameterMap);

        } catch (Exception e) {

            String message = "Error inserting gene_contact for genePk" + genePk + ", contactPk " + contactPk + ": " + e.getLocalizedMessage();
            logger.error(message);
            throw new InterestException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return count;

    }

    /**
     * Activate interest described by {@code gene} and {@code email}.
     *
     * @param invoker The authorised invoker of this request
     * @param gene The mgi accession id of the gene being registgered
     * @param email The email address being registered
     * @param contactCreatedAt The date the contact was created
     * @param geneContactActive The geneContact active flag
     * @param geneContactCreatedAt The date the geneContact was created
     *
     * @return The newly-inserted {@link GeneContact} instance.
     *
     *         <i>NOTE:</i>If the contact and gene has already been registered, geneContactInsertedCount is 0. If
     *         the contact has already been registered, contactsInsertedCount is 0.
     *
     *         @throws InterestException if the gene does not exist
     */
    public GeneContact insertOrUpdateInterestGene(String invoker, String gene, String email, Date contactCreatedAt, int geneContactActive, Date geneContactCreatedAt) throws InterestException {

        String message;

        // Check that the gene exists.
        Gene geneInstance = getGene(gene);
        if (geneInstance == null) {
            message = "Register contact " + email + " for gene " + gene + " failed: Nonexisting gene";
            logSendAction(invoker, null, null, message);
            throw new InterestException(message, HttpStatus.NOT_FOUND);
        }

        Contact contact = updateOrInsertContact(invoker, email, 1, contactCreatedAt);

        // Insert into the gene_contact table.
        insertOrUpdateGeneContact(geneInstance.getPk(), contact.getPk(), geneContactActive, geneContactCreatedAt);

        return getGeneContact(gene, email);
    }

    public void logGeneStatusChangeAction(GeneSent geneSent, int contactPk, Integer genePk, String message) {
        final String query =
                "INSERT INTO log (" +
                    " contact_pk," +
                    " assignment_status_pk," +
                    " conditional_allele_production_status_pk," +
                    " null_allele_production_status_pk," +
                    " phenotyping_status_pk," +
                    " gene_pk," +
                    " gene_sent_pk," +
                    " message)" +
                " VALUES (" +
                    " :contact_pk," +
                    " :assignment_status_pk," +
                    " :conditional_allele_production_status_pk," +
                    " :null_allele_production_status_pk," +
                    " :phenotyping_status_pk," +
                    " :gene_pk," +
                    " :gene_sent_pk," +
                    " :message)";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("contact_pk", contactPk);
        parameterMap.put("assignment_status_pk", geneSent.getAssignmentStatusPk());
        parameterMap.put("conditional_allele_production_status_pk", geneSent.getConditionalAlleleProductionStatusPk());
        parameterMap.put("null_allele_production_status_pk", geneSent.getNullAlleleProductionStatusPk());
        parameterMap.put("phenotyping_status_pk", geneSent.getPhenotypingStatusPk());
        parameterMap.put("gene_pk", genePk);
        parameterMap.put("gene_sent_pk", geneSent.getPk());
        parameterMap.put("message", message);

        jdbcInterest.update(query, parameterMap);
    }

    public void logSendAction(String invoker, Integer genePk, Integer contactPk, String message) {
        final String query = "INSERT INTO log (invoker, contact_pk, gene_pk, message)" +
                            " VALUES (:invoker, :contact_pk, :gene_pk, :message)";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("invoker", invoker);
        parameterMap.put("contact_pk", contactPk);
        parameterMap.put("gene_pk", genePk);
        parameterMap.put("message", message);

        jdbcInterest.update(query, parameterMap);
    }

    /**
     * Remove interest described by {@link GeneContact}.
     *
     * @param gc The {@link GeneContact} instance
     *
     * @throws InterestException if the gene does not exist
     */
    public void removeInterestGene(GeneContact gc) throws InterestException {

        String message;
        final String query = "UPDATE gene_contact SET active = -1 WHERE pk = :pk";

        try {

            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("pk", gc.getPk());

            jdbcInterest.update(query, parameterMap);

        } catch (Exception e) {
            message = "Error removing interest for geneContactPk " + gc.getPk() + " : " + e.getLocalizedMessage();
            throw new InterestException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Try to update the contact. If the update fails because it doesn't exist, insert it.
     *
     * Return the contact (pk is guaranteed to be set)
     *
     * @param email The e-mail address of the contact to be inserted
     *
     * @return the {@link Contact}
     */
    public Contact updateOrInsertContact(String invoker, String email, int active, Date createdAt) throws InterestException {

        final String update = "UPDATE contact SET address = :address, active = :active, created_at = :createdAt WHERE address = :address";
        final String insert = "INSERT INTO contact(address, active, created_at) " +
                              "VALUES (:address, :active, :createdAt)";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("address", email);
        parameterMap.put("active", active <= 0 ? 0 : 1);
        parameterMap.put("createdAt", createdAt);

        try {

            // Try to update. If that fails, insert.
            int updateCount = jdbcInterest.update(update, parameterMap);
            if (updateCount == 0) {

                jdbcInterest.update(insert, parameterMap);
            }

        } catch (Exception e) {

            String message = "Error inserting contact '" + email + "': " + e.getLocalizedMessage() + ".";
            logSendAction(invoker, null, null, message);
            logger.error(message);
            throw new InterestException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return getContact(email);
    }

    /**
     * For every {@link Gene} in {@code genes, first attempt to update it. If the update fails because it's missing,
     * insert the record.
     *
     * @param genes the list of {@link Gene} instances to be used to update the database
     *
     * @return A {@link Map} containing two keyed results:
     * <ul>
     *     <li>
     *         key: "insertCount" ({@Link Integer}) - the number of rows inserted
     *         key: "updateCount" ({@link Integer}) - the number of rows updated
     *      </li>
     * </ul>
     *
     * @throws InterestException
     */
    public Map<String, Integer> updateOrInsertGene(List<Gene> genes) throws InterestException {
        Map<String, Integer> results = new HashMap<>();
        int insertCount = 0;
        int updateCount = 0;
        Date createdAt = new Date();

        for (Gene gene : genes) {

            gene.setCreatedAt(createdAt);

            try {

                Map<String, Object> parameterMap = loadGeneParameterMap(gene);

                // Except for the initial load, most of the time the gene will already exist.
                // Try to update. If that fails because it doesn't yet exist, insert.
                int count = updateGene(parameterMap);
                if (count > 0) {
                    updateCount += count;
                } else {
                    insertCount += insertGene(parameterMap);
                }

            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            }
        }

        results.put("insertCount", insertCount);
        results.put("updateCount", updateCount);

        return results;
    }

    /**
     * Insert the given {@link GeneSent} instance.
     *
     * @param geneSent the {@link GeneSent } instance to be used to update the database
     *
     * @return The {@link GeneSent} instance containing the primary key
     *
     * @throws InterestException
     */
    public GeneSent insertGeneSent(GeneSent geneSent) throws InterestException {

        Map<String, Object> parameterMap = loadSentParameterMap(geneSent);

        KeyHolder          keyholder       = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

        int pk = insertGeneSent(parameterSource, keyholder);

        return getGeneSent(pk);
    }


    // PRIVATE METHODS


    private int insertGene(Map<String, Object> parameterMap) throws InterestException {

        final String columnNames =
                "mgi_accession_id, symbol, assigned_to, assignment_status, assignment_status_date, assignment_status_pk, " +
                "conditional_allele_production_centre, conditional_allele_production_status, conditional_allele_production_status_pk, " +
                "conditional_allele_production_status_date, conditional_allele_production_start_date, " +
                "null_allele_production_centre, null_allele_production_status, null_allele_production_status_pk, " +
                "null_allele_production_status_date, null_allele_production_start_date, " +
                "phenotyping_centre, phenotyping_status, phenotyping_status_date, phenotyping_status_pk, " +
                "number_of_significant_phenotypes, created_at";

        final String columnValues =
                ":mgi_accession_id, :symbol, :assigned_to, :assignment_status, :assignment_status_date, :assignment_status_pk, " +
                ":conditional_allele_production_centre, :conditional_allele_production_status, :conditional_allele_production_status_pk, " +
                ":conditional_allele_production_status_date, :conditional_allele_production_start_date, " +
                ":null_allele_production_centre, :null_allele_production_status, :null_allele_production_status_pk, " +
                ":null_allele_production_status_date, :null_allele_production_start_date, " +
                ":phenotyping_centre, :phenotyping_status, :phenotyping_status_date, :phenotyping_status_pk, " +
                ":number_of_significant_phenotypes, :created_at";

        final String query = "INSERT INTO gene(" + columnNames + ") VALUES (" + columnValues + ")";

        int count = jdbcInterest.update(query, parameterMap);

        return count;
    }

    private int updateGene(Map<String, Object> parameterMap) {

        final String colData =
                // Omit mgi_accession_id in the UPDATE as it is used in the WHERE clause.

                "symbol = :symbol, " +
                "assigned_to = :assigned_to, " + 
                "assignment_status = :assignment_status, " + 
                "assignment_status_date = :assignment_status_date, " +
                "assignment_status_pk = :assignment_status_pk, " +

                "conditional_allele_production_centre = :conditional_allele_production_centre, " +
                "conditional_allele_production_status = :conditional_allele_production_status, " +
                "conditional_allele_production_status_pk = :conditional_allele_production_status_pk, " +
                "conditional_allele_production_status_date = :conditional_allele_production_status_date, " +
                "conditional_allele_production_start_date = :conditional_allele_production_start_date, " +

                "null_allele_production_centre = :null_allele_production_centre, " +
                "null_allele_production_status = :null_allele_production_status, " +
                "null_allele_production_status_pk = :null_allele_production_status_pk, " +
                "null_allele_production_status_date = :null_allele_production_status_date, " +
                "null_allele_production_start_date = :null_allele_production_start_date, " +

                "phenotyping_centre = :phenotyping_centre, " +
                "phenotyping_status = :phenotyping_status, " +
                "phenotyping_status_date = :phenotyping_status_date, " +
                "phenotyping_status_pk = :phenotyping_status_pk, " +

                "number_of_significant_phenotypes = :number_of_significant_phenotypes";

        final String query = "UPDATE gene SET " + colData + " WHERE mgi_accession_id = :mgi_accession_id";

        int count = jdbcInterest.update(query, parameterMap);

        return count;
    }

    private Map<String, Object> loadGeneParameterMap(Gene gene) {

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("mgi_accession_id", gene.getMgiAccessionId());
        parameterMap.put("symbol", gene.getSymbol());
        parameterMap.put("assigned_to", gene.getAssignedTo());
        parameterMap.put("assignment_status", gene.getAssignmentStatus());
        parameterMap.put("assignment_status_date", gene.getAssignmentStatusDate());
        parameterMap.put("assignment_status_pk", gene.getAssignmentStatusPk());

        parameterMap.put("conditional_allele_production_centre", gene.getConditionalAlleleProductionCentre());
        parameterMap.put("conditional_allele_production_status", gene.getConditionalAlleleProductionStatus());
        parameterMap.put("conditional_allele_production_status_pk", gene.getConditionalAlleleProductionStatusPk());
        parameterMap.put("conditional_allele_production_status_date", gene.getConditionalAlleleProductionStatusDate());
        parameterMap.put("conditional_allele_production_start_date", gene.getConditionalAlleleProductionStartDate());

        parameterMap.put("null_allele_production_centre", gene.getNullAlleleProductionCentre());
        parameterMap.put("null_allele_production_status", gene.getNullAlleleProductionStatus());
        parameterMap.put("null_allele_production_status_pk", gene.getNullAlleleProductionStatusPk());
        parameterMap.put("null_allele_production_status_date", gene.getNullAlleleProductionStatusDate());
        parameterMap.put("null_allele_production_start_date", gene.getNullAlleleProductionStartDate());

        parameterMap.put("phenotyping_centre", gene.getPhenotypingCentre());
        parameterMap.put("phenotyping_status", gene.getPhenotypingStatus());
        parameterMap.put("phenotyping_status_date", gene.getPhenotypingStatusDate());
        parameterMap.put("phenotyping_status_pk", gene.getPhenotypingStatusPk());

        parameterMap.put("number_of_significant_phenotypes", gene.getNumberOfSignificantPhenotypes());

        parameterMap.put("created_at", gene.getCreatedAt());

        return parameterMap;
    }

    private int insertGeneSent(SqlParameterSource parameterSource, KeyHolder keyholder) throws InterestException {

        // Try to insert the row into gene_sent. If the gene_contact_pk already exists, the INSERT operation is ignored.
        final String columnNames =
                "subject, body, gene_contact_pk, " +
                "assignment_status_pk, conditional_allele_production_status_pk, null_allele_production_status_pk, phenotyping_status_pk, " +
                "created_at, sent_at";

        final String columnValues =
                ":subject, :body, :gene_contact_pk, " +
                ":assignment_status_pk, :conditional_allele_production_status_pk, :null_allele_production_status_pk, :phenotyping_status_pk, " +
                ":created_at, :sent_at";

        final String query = "INSERT INTO gene_sent(" + columnNames + ") VALUES (" + columnValues + ")";

        int count = jdbcInterest.update(query, parameterSource, keyholder);
        if (count > 0) {
            return keyholder.getKey().intValue();
        }

        throw new InterestException("Unable to get primary key after INSERT.");
    }

    private int updateGeneSent(GeneSent geneSent, Map<String, Object> parameterMap) {

        final String colData =
                // Omit gene_contact_pk in the UPDATE as it is used in the WHERE clause.

                "subject = :subject, " +
                "body = :body, " +
                "gene_contact_pk = :gene_contact_pk, " +

                "assignment_status_pk = :assignment_status_pk, " +
                "conditional_allele_production_status_pk = :conditional_allele_production_status_pk, " +
                "null_allele_production_status_pk = :null_allele_production_status_pk, " +
                "phenotyping_status_pk = :phenotyping_status_pk, " +

                "sent_at = :sent_at";

        String query = "UPDATE gene_sent SET " + colData + " WHERE gene_contact_pk = :gene_contact_pk";

        int count = jdbcInterest.update(query, parameterMap);
        if (count > 0) {

            // Get and update the gene_sent primary key.
            query = "SELECT pk FROM gene_sent WHERE gene_contact_pk = :gene_contact_pk";
            int pk = jdbcInterest.queryForObject(query, parameterMap, Integer.class);
            geneSent.setPk(pk);
        }

        return count;
    }

    private Map<String, Object> loadSentParameterMap(GeneSent geneSent) {

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("subject", geneSent.getSubject());
        parameterMap.put("body", geneSent.getBody());
        parameterMap.put("gene_contact_pk", geneSent.getGeneContactPk());

        parameterMap.put("assignment_status_pk", geneSent.getAssignmentStatusPk());
        parameterMap.put("conditional_allele_production_status_pk", geneSent.getConditionalAlleleProductionStatusPk());
        parameterMap.put("null_allele_production_status_pk", geneSent.getNullAlleleProductionStatusPk());
        parameterMap.put("phenotyping_status_pk", geneSent.getPhenotypingStatusPk());

        parameterMap.put("created_at", (geneSent.getCreatedAt() == null ? new Date() : geneSent.getCreatedAt()));
        parameterMap.put("sent_at", geneSent.getSentAt());

        return parameterMap;
    }
}