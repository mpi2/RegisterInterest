package org.mousephenotype.ri.core;

import org.mousephenotype.ri.core.entities.*;
import org.mousephenotype.ri.core.rowmappers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.mousephenotype.ri.core.exceptions.InterestException;

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
     * Return a list of {@link Interest} instances matching {@code emailAddress} and (if not null or empty) {@code mgiAccessionId}
     *
     * @param emailAddress The contact's email address
     * @param mgiAccessionId (optional) the mgi accession id. If not null or empty, the query will include the gene; otherwise,
     *                       all associated genes are returned.
     *
     * @return a list of matching {@link Interest} instances
     */
    public List<Interest> getGenesForContact(String emailAddress, String mgiAccessionId) {

        String query =
                "SELECT\n" +
                        "  c.pk       AS contact_pk,\n" +
                        "  c.address,\n" +
                        "  c.active,\n" +
                        "  g.pk       AS gene_pk,\n" +
                        "  g.mgi_accession_id,\n" +
                        "  gc.created_at,\n" +
                        "  gc.updated_at\n" +
                        "FROM gene_contact gc\n" +
                        "JOIN gene    g ON g.pk = gc.gene_pk\n" +
                        "JOIN contact c ON c.pk = gc.contact_pk\n" +
                        "WHERE c.address = :address";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("address", emailAddress);

        if ((mgiAccessionId != null) && ( ! mgiAccessionId.isEmpty())) {
            query += " AND g.mgi_accession_id = :mgiAccessionId";
            parameterMap.put("mgiAccessionId", mgiAccessionId);
        }


        List<Interest> interestList = jdbcInterest.query(query, parameterMap, new InterestRowMapper());

        return interestList;
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
     * Return a list of {@link Interest} instances matching {@code mgiAccessionId} and (if not null or empty) {@code emailAddress}
     *
     * @param mgiAccessionId The gene's MGI accession id
     * @param emailAddress The contact's email address If not null or empty, the query will include the email address; otherwise,
     *                     all associated contacts are returned.
     *
     * @return a list of matching {@link Interest} instances
     */
    public List<Interest> getContactsForGene(String mgiAccessionId, String emailAddress) {

         String query =
                "SELECT\n" +
                        "  c.pk       AS contact_pk,\n" +
                        "  c.address,\n" +
                        "  c.active,\n" +
                        "  g.pk       AS gene_pk,\n" +
                        "  g.mgi_accession_id,\n" +
                        "  gc.created_at,\n" +
                        "  gc.updated_at\n" +
                        "FROM gene_contact gc\n" +
                        "JOIN gene g ON g.pk = gc.gene_pk\n" +
                        "JOIN contact c ON c.pk = gc.contact_pk\n" +
                        "WHERE g.mgi_accession_id = :mgiAccessionId";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("mgiAccessionId", mgiAccessionId);

        if ((emailAddress != null) && ( ! emailAddress.isEmpty())) {
            query += " AND c.address = :address";
            parameterMap.put("address", emailAddress);
        }

        List<Interest> interestList = jdbcInterest.query(query, parameterMap, new InterestRowMapper());

        return interestList;
    }

    /**
     * Given an emailAddress and an mgiAccessionId, returns the matching {@link Interest} instance if found; null
     * otherwise
     *
     * @param emailAddress The email address of the desired {@link Interest} instance
     * @param mgiAccessionId The mgi accession id of the desired {@link Interest} instance
     *
     * @return
     *
     * @throws InterestException if either input parameter is null or empty
     */
    public Interest getInterest(String emailAddress, String mgiAccessionId) throws InterestException {

        if ((emailAddress == null) || (emailAddress.trim().isEmpty()) ||
            ((mgiAccessionId == null) || mgiAccessionId.trim().isEmpty())) {
            throw new InterestException(HttpStatus.NOT_FOUND);
        }

        final String query =
                "SELECT\n" +
                        "  c.pk       AS contact_pk,\n" +
                        "  c.address,\n" +
                        "  c.active,\n" +
                        "  g.pk       AS gene_pk,\n" +
                        "  g.mgi_accession_id,\n" +
                        "  gc.created_at,\n" +
                        "  gc.updated_at\n" +
                        "FROM gene_contact gc\n" +
                        "JOIN gene g ON g.pk = gc.gene_pk\n" +
                        "JOIN contact c ON c.pk = gc.contact_pk\n" +
                        "WHERE c.address = :emailAddress AND g.mgi_accession_id = :mgiAccessionId";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("emailAddress", emailAddress);
        parameterMap.put("mgiAccessionId", mgiAccessionId);

        List<Interest> interestList = jdbcInterest.query(query, parameterMap, new InterestRowMapper());

        return (interestList.isEmpty() ? null : interestList.get(0));
    }

    /**
     *
     * @return A {@link Map} of all {@link GeneSent} instances, indexed by gene_contact_pk
     */
    public Map<Integer, GeneSent> getSent() {

        Map<Integer, GeneSent> sentMap = new HashMap<>();

        final String query = "SELECT * FROM gene_sent";
        Map<String, Object> parameterMap = new HashMap<>();

        List<GeneSent> geneSentList = jdbcInterest.query(query, parameterMap, new SentRowMapper());
        for (GeneSent geneSent : geneSentList) {
            sentMap.put(geneSent.getGeneContactPk(), geneSent);
        }

        return sentMap;
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
     * Try to insert the contact. Return the count of inserted contacts.
     *
     * @param contact A {@link String} containing the e-mail address of the contact to be inserted
     *
     * @return the count of inserted contacts.
     */
    public int insertContact(String contact) throws InterestException {
        int count = 0;
        final String query = "INSERT INTO contact(address, active, created_at) " +
                "VALUES (:address, 1, :created_at)";

        // Insert contact. Ignore any duplicates.
        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("address", contact);
            parameterMap.put("created_at", new Date());

            count += jdbcInterest.update(query, parameterMap);

        } catch (DuplicateKeyException e) {

        } catch (Exception e) {
            logger.error("Error inserting contact {}: {}. Record skipped...", contact, e.getLocalizedMessage());
        }

        return count;
    }

    /**
     * Try to insert {@link Interest} object into the gene_contact table. Return the count of inserted rows.
     *
     * @param interest A {@link String} containing the e-mail address of the geneContact to be inserted
     *
     * @return the count of inserted geneContacts.
     */
    public int insertGeneContact(Interest interest) throws InterestException {
        int count = 0;
        final String query = "INSERT INTO gene_contact(contact_pk, gene_pk, created_at) " +
                "VALUES (:contact_pk, :gene_pk, :created_at)";

        // Insert gene_contact. Ignore any duplicates.
        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("contact_pk", interest.getContactPk());
            parameterMap.put("gene_pk", interest.getGenePk());
            parameterMap.put("created_at", new Date());

            count += jdbcInterest.update(query, parameterMap);

        } catch (DuplicateKeyException e) {

        } catch (Exception e) {
            logger.error("Error inserting gene_contact {}: {}. Record skipped...", interest, e.getLocalizedMessage());
        }

        return count;
    }

    /**
     * Try to insert the gene. Return the count of inserted genes.
     *
     * @param mgi_accession_id A {@link String} containing the MGI accession id of the gene to be inserted
     *
     * @return the count of inserted genes.
     */
    public int insertGene(String mgi_accession_id) throws InterestException {
        int count = 0;
        final String query = "INSERT INTO gene(mgi_accession_id, created_at) " +
                "VALUES (:mgi_accession_id, :created_at)";

        // Insert gene. Ignore any duplicates.
        try {

            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put(":mgi_accession_id", mgi_accession_id);
            parameterMap.put("created_at", new Date());

            count += jdbcInterest.update(query, parameterMap);

        } catch (DuplicateKeyException e) {

        } catch (Exception e) {
            logger.error("Error inserting MGI accession id {}: {}. Record skipped...", mgi_accession_id, e.getLocalizedMessage());
        }

        return count;
    }

    /**
     * Try to insert the {@link Interest} object.
     *
     * @param interest An {@link Interest} instance containing the contact email address and the MGI accession id of
     *                 the instance to be inserted. If the mgi accession id doesn't exist, an InterestException is thrown.
     *
     * @return a map containing the count of inserted contacts (key = contactsInsertedCount) and the count of inserted
     *         gene_contact rows (key = geneContactInsertedCount).
     *
     *         <i>NOTE:</i>If the contact and gene has already been registered, geneContactInsertedCount is 0. If
     *         the contact has already been registered, contactsInsertedCount is 0.
     *
     *         @throws InterestException if the gene does not exist
     */
    public Map<String, Integer> insertInterest(Interest interest) throws InterestException {
        Map<String, Integer> results = new HashMap<>();
        Integer geneContactInsertedCount = 0;
        Integer contactsInsertedCount = 0;
        results.put("geneContactInsertedCount", geneContactInsertedCount);
        results.put("contactsInsertedCount", contactsInsertedCount);

        // Check that the mapping doesn't already exist.
        if (getInterest(interest.getAddress(), interest.getMgiAccessionId()) != null) {
            return results;
        }

        // Check that the gene exists. If it doesn't, throw an exception and exit.
        if (getGene(interest.getMgiAccessionId()) == null) {
            throw new InterestException(HttpStatus.NOT_FOUND);
        }

        // If the contact doesn't yet exist, create it.
        contactsInsertedCount += insertContact(interest.getAddress());

        // Make sure the pks exist.
        if (interest.getContactPk() == null) {
            interest.setContactPk(getContact(interest.getAddress()).getPk());
        }
        if (interest.getGenePk() == null) {
            interest.setGenePk(getGene(interest.getMgiAccessionId()).getPk());
        }

        // Insert into the gene_contact table.
        geneContactInsertedCount = insertGeneContact(interest);

        results.put("geneContactInsertedCount", geneContactInsertedCount);
        results.put("contactsInsertedCount", contactsInsertedCount);

        return results;
    }

    public void logWebServiceAction(int contact_pk, Integer gene_pk, String message) {
        final String query = "INSERT INTO log (contact_pk, gene_pk, message)" +
                            " VALUES (:contact_pk, :gene_pk, :message)";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("contact_pk", contact_pk);
        parameterMap.put("gene_pk", gene_pk);
        parameterMap.put("message", message);

        jdbcInterest.update(query, parameterMap);
    }

    /**
     * Try to remove the {@link Interest} object.
     *
     * @param interest An {@link Interest} instance containing the contact email address and the MGI accession id of
     *                 the instance to be removed. If the no mapping is found for this emailAddress and mgiAccessionId,
     *                 an InterestException is thrown.
     *
     *  @throws InterestException if the gene does not exist
     */
    public void removeInterest(Interest interest) throws InterestException {
        final String query = "DELETE FROM gene_contact WHERE contact_pk = :contactPk AND gene_pk = :genePk";

        try {
            Interest localInterest = getInterest(interest.getAddress(), interest.getMgiAccessionId());
            if (localInterest == null) {
                throw new InterestException(HttpStatus.NOT_FOUND);
            }

            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("contactPk", localInterest.getContactPk());
            parameterMap.put("genePk", localInterest.getGenePk());

            jdbcInterest.update(query, parameterMap);

        } catch (Exception e) {
            logger.error("Error removing interest {} {}: {}. Record skipped...", interest.getAddress(), interest.getMgiAccessionId(), e.getLocalizedMessage());
            throw new InterestException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * For every {@link Gene} in {@code genes, first attempt to update it. If the update fails because it's missing,
     * insert the record.
     *
     * @param genes the list of {@link Gene} instances to be used to update the database
     *
     * @return The number of instances inserted/updated
     *
     * @throws InterestException
     */
    public int updateOrInsertGene(List<Gene> genes) throws InterestException {
        int count = 0;

        Date createdAt = new Date();

        for (Gene gene : genes) {

            gene.setCreatedAt(createdAt);

            try {

                Map<String, Object> parameterMap = loadGeneParameterMap(gene);

                // Except for the initial load, most of the time the gene will already exist.
                // Try to update. If that fails because it doesn't yet exist, insert.
                int updateCount = updateGene(parameterMap);
                if (updateCount == 0) {
                    updateCount = insertGene(parameterMap);
                }

                count += updateCount;

            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            }
        }

        return count;
    }

    /**
     * For every {@link GeneSent} in {@code geneSentList, first attempt to update it. If the update fails because it's missing,
     * insert the record.
     *
     * @param geneSentList the list of {@link GeneSent } instances to be used to update the database
     *
     * @return The number of instances inserted/updated
     *
     * @throws InterestException
     */
    public int updateOrInsertSent(List<GeneSent> geneSentList) throws InterestException {
        int count = 0;

        for (GeneSent geneSent : geneSentList) {

            try {

                Map<String, Object> parameterMap = loadSentParameterMap(geneSent);

                // Except for the initial load, most of the time the row will already exist.
                // Try to update. If that fails because it doesn't yet exist, insert.
                int updateCount = updateSent(parameterMap);
                if (updateCount == 0) {
                    updateCount = insertSent(parameterMap);
                }

                count += updateCount;

            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            }
        }

        return count;
    }


    // PRIVATE METHODS


    private int insertGene(Map<String, Object> parameterMap) throws InterestException {

        // Try to insert the row into gene. If the mgi accession id already exists, the INSERT operation is ignored.
        final String columnNames =
                "mgi_accession_id, symbol, assigned_to, assignment_status, assignment_status_date, assignment_status_pk, " +
                "conditional_allele_production_centre, conditional_allele_production_status, conditional_allele_production_status_date, conditional_allele_production_status_pk, " +
                "null_allele_production_centre, null_allele_production_status, null_allele_production_status_date, null_allele_production_status_pk, " +
                "phenotyping_centre, phenotyping_status, phenotyping_status_date, phenotyping_status_pk, " +
                "number_of_significant_phenotypes, created_at";

        final String columnValues =
                ":mgi_accession_id, :symbol, :assigned_to, :assignment_status, :assignment_status_date, :assignment_status_pk, " +
                ":conditional_allele_production_centre, :conditional_allele_production_status, :conditional_allele_production_status_date, :conditional_allele_production_status_pk, " +
                ":null_allele_production_centre, :null_allele_production_status, :null_allele_production_status_date, :null_allele_production_status_pk, " +
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
                "conditional_allele_production_status_date = :conditional_allele_production_status_date, " +
                "conditional_allele_production_status_pk = :conditional_allele_production_status_pk, " +

                "null_allele_production_centre = :null_allele_production_centre, " +
                "null_allele_production_status = :null_allele_production_status, " +
                "null_allele_production_status_date = :null_allele_production_status_date, " +
                "null_allele_production_status_pk = :null_allele_production_status_pk, " +

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
        parameterMap.put("conditional_allele_production_status_date", gene.getConditionalAlleleProductionStatusDate());
        parameterMap.put("conditional_allele_production_status_pk", gene.getConditionalAlleleProductionStatusPk());

        parameterMap.put("null_allele_production_centre", gene.getNullAlleleProductionCentre());
        parameterMap.put("null_allele_production_status", gene.getNullAlleleProductionStatus());
        parameterMap.put("null_allele_production_status_date", gene.getNullAlleleProductionStatusDate());
        parameterMap.put("null_allele_production_status_pk", gene.getNullAlleleProductionStatusPk());

        parameterMap.put("phenotyping_centre", gene.getPhenotypingCentre());
        parameterMap.put("phenotyping_status", gene.getPhenotypingStatus());
        parameterMap.put("phenotyping_status_date", gene.getPhenotypingStatusDate());
        parameterMap.put("phenotyping_status_pk", gene.getPhenotypingStatusPk());

        parameterMap.put("number_of_significant_phenotypes", gene.getNumberOfSignificantPhenotypes());

        parameterMap.put("created_at", gene.getCreatedAt());

        return parameterMap;
    }

    private int insertSent(Map<String, Object> parameterMap) throws InterestException {

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

        int count = jdbcInterest.update(query, parameterMap);

        return count;
    }

    private int updateSent(Map<String, Object> parameterMap) {

        final String colData =
                // Omit gene_contact_pk in the UPDATE as it is used in the WHERE clause.

                "subject = :subject, " +
                "gene_contact_pk = :gene_contact_pk, " +

                "assignment_status_pk = :assignment_status_pk, " +
                "conditional_allele_production_status_pk = :conditional_allele_production_status_pk, " +
                "null_allele_production_status_pk = :null_allele_production_status_pk, " +
                "phenotyping_status_pk = :phenotyping_status_pk, " +

                "sent_at = :sent_at";

        final String query = "UPDATE gene_sent SET " + colData + " WHERE gene_contact_pk = :gene_contact_pk";

        int count = jdbcInterest.update(query, parameterMap);

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

        parameterMap.put("created_at", geneSent.getCreatedAt());
        parameterMap.put("sent_at", geneSent.getSentAt());

        return parameterMap;
    }
}