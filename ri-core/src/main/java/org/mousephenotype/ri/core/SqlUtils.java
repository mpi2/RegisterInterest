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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                        "  cg.updated_at\n" +
                        "FROM contact_gene cg\n" +
                        "JOIN gene    g ON g.pk = cg.gene_pk\n" +
                        "JOIN contact c ON c.pk = cg.contact_pk\n" +
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

    public Map<String, Component> getComponents() {
        Map<String, Component> componentMap = new HashMap<>();

        final String query = "SELECT * FROM component";

        List<Component> list = jdbcInterest.query(query, new ComponentRowMapper());
        for (Component component : list) {
            componentMap.put(component.getName(), component);
        }

        return componentMap;
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
                        "  cg.updated_at\n" +
                        "FROM contact_gene cg\n" +
                        "JOIN gene g ON g.pk = cg.gene_pk\n" +
                        "JOIN contact c ON c.pk = cg.contact_pk\n" +
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
                        "  cg.updated_at\n" +
                        "FROM contact_gene cg\n" +
                        "JOIN gene g ON g.pk = cg.gene_pk\n" +
                        "JOIN contact c ON c.pk = cg.contact_pk\n" +
                        "WHERE c.address = :emailAddress AND g.mgi_accession_id = :mgiAccessionId";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("emailAddress", emailAddress);
        parameterMap.put("mgiAccessionId", mgiAccessionId);

        List<Interest> interestList = jdbcInterest.query(query, parameterMap, new InterestRowMapper());

        return (interestList.isEmpty() ? null : interestList.get(0));
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
        final String query = "INSERT INTO contact(address, active) " +
                "VALUES (:address, 1)";

        // Insert contact. Ignore any duplicates.
        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("address", contact);

            count += jdbcInterest.update(query, parameterMap);

        } catch (DuplicateKeyException e) {

        } catch (Exception e) {
            logger.error("Error inserting contact {}: {}. Record skipped...", contact, e.getLocalizedMessage());
        }

        return count;
    }

    /**
     * Try to insert {@link Interest} object into the contact_gene table. Return the count of inserted rows.
     *
     * @param interest A {@link String} containing the e-mail address of the contactGene to be inserted
     *
     * @return the count of inserted contactGenes.
     */
    public int insertContactGene(Interest interest) throws InterestException {
        int count = 0;
        final String query = "INSERT INTO contact_gene(contact_pk, gene_pk) " +
                "VALUES (:contact_pk, :gene_pk)";

        // Insert contact_gene. Ignore any duplicates.
        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("contact_pk", interest.getContactPk());
            parameterMap.put("gene_pk", interest.getGenePk());

            count += jdbcInterest.update(query, parameterMap);

        } catch (DuplicateKeyException e) {

        } catch (Exception e) {
            logger.error("Error inserting contact_gene {}: {}. Record skipped...", interest, e.getLocalizedMessage());
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
        final String query = "INSERT INTO gene(mgi_accession_id) " +
                "VALUES (:mgi_accession_id)";

        // Insert gene. Ignore any duplicates.
        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put(":mgi_accession_id", mgi_accession_id);

            count += jdbcInterest.update(query, parameterMap);

        } catch (DuplicateKeyException e) {

        } catch (Exception e) {
            logger.error("Error inserting MGI accession id {}: {}. Record skipped...", mgi_accession_id, e.getLocalizedMessage());
        }

        return count;
    }

    public int insertGeneStatus(List<GeneStatusChange> geneStatusList) {
        int count = 0;

        return count;
    }

    /**
     * Try to insert the {@link Interest} object.
     *
     * @param interest An {@link Interest} instance containing the contact email address and the MGI accession id of
     *                 the instance to be inserted. If the mgi accession id doesn't exist, an InterestException is thrown.
     *
     * @return a map containing the count of inserted contacts (key = contactsInsertedCount) and the count of inserted
     *         contact_gene rows (key = contactGeneInsertedCount).
     *
     *         <i>NOTE:</i>If the contact and gene has already been registered, contactGeneInsertedCount is 0. If
     *         the contact has already been registered, contactsInsertedCount is 0.
     *
     *         @throws InterestException if the gene does not exist
     */
    public Map<String, Integer> insertInterest(Interest interest) throws InterestException {
        Map<String, Integer> results = new HashMap<>();
        Integer contactGeneInsertedCount = 0;
        Integer contactsInsertedCount = 0;
        results.put("contactGeneInsertedCount", contactGeneInsertedCount);
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

        // Insert into the contact_gene table.
        contactGeneInsertedCount = insertContactGene(interest);

        results.put("contactGeneInsertedCount", contactGeneInsertedCount);
        results.put("contactsInsertedCount", contactsInsertedCount);

        return results;
    }

    public void logWebServiceAction(int contact_pk, int component_pk, Integer gene_pk, String message) {
        final String query = "INSERT INTO log (contact_pk, component_pk, gene_pk, message)" +
                            " VALUES (:contact_pk, :component_pk, :gene_pk, :message)";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("contact_pk", contact_pk);
        parameterMap.put("component_pk", component_pk);
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
        final String query = "DELETE FROM contact_gene WHERE contact_pk = :contactPk AND gene_pk = :genePk";

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
}