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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 12/05/2017.
 */
public class SqlUtils {
    private static final Logger logger = LoggerFactory.getLogger(SqlUtils.class);

    private static final Integer INITIAL_POOL_CONNECTIONS = 1;


    @NotNull
    private NamedParameterJdbcTemplate jdbcInterest;
    
    @Inject
    public SqlUtils(NamedParameterJdbcTemplate jdbcInterest) {
        this.jdbcInterest = jdbcInterest;
    }


    /**
     * Add {@code role} to {@code emailAddress}
     *
     * @param emailAddress The email addresss to be inserted
     * @param role The role to be added
     * @throws InterestException (HttpStatus.INTERNAL_SERVER_ERROR if either the email address doesn't exist or this
     *                           emailAddress/role pair already exists
     */
    public void addRole(String emailAddress, RIRole role) throws InterestException {

        String message;

        Contact contact = getContact(emailAddress);
        if (contact == null) {
            message = "addRole(): Invalid contact " + emailAddress + ".";
            logger.error(message);
            throw new InterestException(message, HttpStatus.NOT_FOUND);
        }

        final String insert = "INSERT INTO contact_role (contact_pk, role, created_at)" +
                              "VALUES (:contactPk, :role, :createdAt)";

        Date createdAt = new Date();

        Map<String, Object> parameterMap = new HashMap<>();
        try {
            parameterMap.put("contactPk", contact.getPk());
            parameterMap.put("role", role.toString());
            parameterMap.put("createdAt", createdAt);

            int rowCount = jdbcInterest.update(insert, parameterMap);
            if (rowCount < 1) {
                message = "Unable to add role " + role.toString() + " for contact " + emailAddress + ".";
                logger.error(message);
                throw new InterestException(message, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {

            message = "Error inserting contact_role for " + emailAddress + ": " + e.getLocalizedMessage();
            logger.error(message);
            throw new InterestException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    /**
     * Within the scope of a single transaction, insert the contact, then the default USER role
     *
     * @param emailAddress The email addresss to be inserted
     * @param encryptedPassword The encrypted password to be inserted
     * @throws InterestException (HttpStatus.INTERNAL_SERVER_ERROR if either the email address or the role already exists
     */
    @Transactional
    public void createAccount(String emailAddress, String encryptedPassword) throws InterestException {

        final String ERROR_MESSAGE = "We were unable to register you with the specified e-mail address. Please contact the EBI mouse informatics helpdesk for assistance.";
        final String insert = "INSERT INTO contact(address, password, password_expired, account_locked, created_at) " +
                              "VALUES (:address, :password, :passwordExpired, :accountLocked, :createdAt)";
        int pk;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("address", emailAddress);
        parameterMap.put("password", encryptedPassword);
        parameterMap.put("passwordExpired", 0);
        parameterMap.put("accountLocked", 0);
        parameterMap.put("createdAt", new Date());

        try {

            // Insert the contact.
            KeyHolder          keyholder       = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
            pk = jdbcInterest.update(insert, parameterSource, keyholder);
            if (pk < 1) {
                logger.error("Contact {} already exists.", emailAddress);
                throw new InterestException(ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {

            logger.error("Exception adding contact {}. Reason: {}", emailAddress, e.getLocalizedMessage());
            throw new InterestException(ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Add the USER role to the database.
        addRole(emailAddress, RIRole.USER);
    }

    public void createSpringBatchTables(DataSource datasource) {

        logger.info("Creating SPRING BATCH tables");
        org.springframework.core.io.Resource r = new ClassPathResource("org/springframework/batch/core/schema-mysql.sql");
        ResourceDatabasePopulator p = new ResourceDatabasePopulator(r);
        p.execute(datasource);
    }

    public void deleteAllContactGenes() {
        String delete = "DELETE FROM contact_gene";
        Map<String, Object> parameterMap = new HashMap<>();
        jdbcInterest.update(delete, parameterMap);
    }

    public void deleteAllContactRoles() {
        String delete = "DELETE FROM contact_role";
        Map<String, Object> parameterMap = new HashMap<>();
        jdbcInterest.update(delete, parameterMap);
    }

    public void deleteAllContacts() {
        String delete = "DELETE FROM contact";
        Map<String, Object> parameterMap = new HashMap<>();
        jdbcInterest.update(delete, parameterMap);
    }

    @Transactional
    public void deleteContact(String emailAddress) throws InterestException {

        String message;

        Contact contact = getContact(emailAddress);
        if (contact == null) {
            message = "deleteContact(): Invalid contact " + emailAddress + ".";
            logger.error(message);
            throw new InterestException(message, HttpStatus.NOT_FOUND);
        }

        String delete;
        Map<String, Object> parameterMap = new HashMap<>();

        // Delete all matching emailAddress from contact_gene.
        delete = "DELETE FROM contact_gene WHERE contact_pk = :contactPk";
        parameterMap.put("contactPk", contact.getPk());
        jdbcInterest.update(delete, parameterMap);

        // Delete all matching emailAddress from gene_sent (GDPR).
        delete = "DELETE FROM gene_sent WHERE address = :address";
        parameterMap.put("address", contact.getEmailAddress());
        jdbcInterest.update(delete, parameterMap);

        // Delete all matching emailAddress from gene_sent_summary (GDPR).
        delete = "DELETE FROM gene_sent_summary WHERE address = :address";
        parameterMap.put("address", contact.getEmailAddress());
        jdbcInterest.update(delete, parameterMap);

        // Delete all matching emailAddress from reset_credentials
        deleteResetCredentialsByEmailAddress(emailAddress);

        // Delete all matching emailAddress from contact_role.
        delete = "DELETE FROM contact_role WHERE contact_pk = :contactPk";
        jdbcInterest.update(delete, parameterMap);

        // Delete all matching emailAddress from contact
        delete = "DELETE FROM contact WHERE pk = :contactPk";
        jdbcInterest.update(delete, parameterMap);
    }

    /**
     * Delete the row in reset_credentials identified by {@code emailAddress}. If no such email address exists, no
     * rows are deleted.
     *
     * @param emailAddress key to use for delete
     *
     * @return the number of rows deleted
     */
    public int deleteResetCredentialsByEmailAddress(String emailAddress) {

        int rowsDeleted = 0;

        String delete = "DELETE FROM reset_credentials WHERE email_address = :emailAddress";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", emailAddress);

        try {
            rowsDeleted = jdbcInterest.update(delete, parameterMap);
        } catch (Exception e) {
            // Ignore any errors
        }

        return rowsDeleted;
    }

    /**
     * Delete {@code role} from contact identified by {@code emailAddress}
     *
     * @param emailAddress The email address
     * @param role the role
     *
     * @throws InterestException (HttpStatus.INTERNAL_SERVER_ERROR if either the email address doesn't exist or this
     *                           emailAddress/role pair doesn't exist
     */
    public void deleteRole(String emailAddress, RIRole role) throws InterestException {

        String message;

        Contact contact = getContact(emailAddress);
        if (contact == null) {
            message = "deleteRole(): Invalid contact " + emailAddress + ".";
            logger.error(message);
            throw new InterestException(message, HttpStatus.NOT_FOUND);
        }

        final String delete = "DELETE FROM contact_role WHERE contact_pk = :contactPk AND role = :role";

        Map<String, Object> parameterMap = new HashMap<>();

        try {
            parameterMap.put("contactPk", contact.getPk());
            parameterMap.put("role", role.toString());

            int rowCount = jdbcInterest.update(delete, parameterMap);
            if (rowCount < 1) {
                message = "Unable to delete role " + role.toString() + " for contact " + emailAddress + ".";
                logger.error(message);
                throw new InterestException(message, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {

            message = "Error deleting contact_role for " + emailAddress + ": " + e.getLocalizedMessage();
            logger.error(message);
            throw new InterestException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static DataSource getConfiguredDatasource(String url, String username, String password) {
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setInitialSize(INITIAL_POOL_CONNECTIONS);
        ds.setMaxActive(50);
        ds.setMinIdle(INITIAL_POOL_CONNECTIONS);
        ds.setMaxIdle(INITIAL_POOL_CONNECTIONS);
        ds.setTestOnBorrow(true);
        ds.setValidationQuery("SELECT 1");
        ds.setValidationInterval(5000);
        ds.setMaxAge(30000);
        ds.setMaxWait(35000);
        ds.setTestWhileIdle(true);
        ds.setTimeBetweenEvictionRunsMillis(5000);
        ds.setMinEvictableIdleTimeMillis(5000);
        ds.setValidationInterval(30000);
        ds.setRemoveAbandoned(true);
        ds.setRemoveAbandonedTimeout(10000); // 10 seconds before abandoning a query

        try {
            logger.info("Using cdasource database {} with initial pool size {}. URL: {}", ds.getConnection().getCatalog(), ds.getInitialSize(), url);

        } catch (Exception e) {

            System.err.println(e.getLocalizedMessage());
            e.printStackTrace();
        }

        return ds;
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
        Contact contact = (contacts.isEmpty() ? null : contacts.get(0));
        if (contact != null) {
            contact.setRoles(getContactRoles(emailAddress));
        }

        return contact;
    }

    /**
     *
     * @param gene The mgi accession id of the gene of interest
     * @param email the email address of interest
     * @return the {@link ContactGene} instance, if found; null otherwise
     */
    @Deprecated
    // FIXME IS THIS USED ANYWHERE? IF NOT, GET RID OF IT.
    public ContactGene getContactGene(String gene, String email) {
        String query =
                "SELECT * FROM contact_gene cg\n" +
                        "JOIN gene g ON g.pk = cg.gene_pk\n" +
                        "JOIN contact c ON c.pk = cg.contact_pk\n" +
                        "WHERE g.mgi_accession_id = :gene AND c.address = :email";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("gene", gene);
        parameterMap.put("email", email);

        List<ContactGene> list = jdbcInterest.query(query, parameterMap, new ContactGeneRowMapper());

        return (list.isEmpty() ? null : list.get(0));
    }

    /**
     *
     * @return a list of {@link ContactGeneReportRow}. Needed by iMits.
     */
    public List<ContactGeneReportRow> getContactGeneReportRow() {
        List<ContactGeneReportRow> report;

        final String query = "SELECT\n" +
                "  c.address          AS contact_email,\n" +
                "  c.created_at       AS contact_created_at,\n" +
                "  g.symbol           AS marker_symbol,\n" +
                "  g.mgi_accession_id AS mgi_accession_id,\n" +
                "  cg.created_at      AS gene_interest_created_at\n" +
                "FROM contact c\n" +
                "JOIN contact_gene cg ON cg.contact_pk = c.pk\n" +
                "JOIN gene         g  ON g.pk = cg.gene_pk\n" +
                "ORDER BY c.address, g.symbol;";

        Map<String, Object> parameterMap = new HashMap<>();
        report = jdbcInterest.query(query, parameterMap, new ContactGeneReportRowRowMapper());

        return report;
    }

    /**
     *
     * @return a {@link List} of all {@link ContactGene} entries
     */
    public List<ContactGene> getContactGenes() {
        final String query = "SELECT * FROM contact_gene";

        Map<String, Object> parameterMap = new HashMap<>();

        return jdbcInterest.query(query, parameterMap, new ContactGeneRowMapper());
    }

    /**
     * @param emailAddress The contact for which the gene list is desired
     * @return a {@link List} of all {@link ContactGene} entries
     */
    public List<ContactGene> getContactGenes(String emailAddress) {
        final String query = "SELECT * FROM contact_gene";

        Map<String, Object> parameterMap = new HashMap<>();

        return jdbcInterest.query(query, parameterMap, new ContactGeneRowMapper());
    }

    public Collection<GrantedAuthority> getContactRoles(String emailAddress) {

        ArrayList<GrantedAuthority> roles = new ArrayList<>();

        final String query = "SELECT cr.* FROM contact_role cr " +
                "JOIN contact c ON c.pk = cr.contact_pk " +
                "WHERE c.address = :emailAddress";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", emailAddress);

        List<ContactRole> results = jdbcInterest.query(query, parameterMap, new ContactRoleRowMapper());
        for (ContactRole result : results) {
            roles.add(result.getAuthority());
        }

        return roles;
    }

    public List<Contact> getContacts() {
        final String query = "SELECT * FROM contact";

        Map<Integer, Contact> contactMap = new HashMap<>();
        List<Contact> contactList = jdbcInterest.query(query, new HashMap<String, Object>(), new ContactRowMapper());

        return contactList;
    }

    public Map<Integer, Contact> getContactsIndexedByContactPk() {
        final String query = "SELECT * FROM contact";

        Map<Integer, Contact> contactMap = new HashMap<>();
        List<Contact> contactList = jdbcInterest.query(query, new HashMap<String, Object>(), new ContactRowMapper());
        for (Contact contact : contactList) {
            contactMap.put(contact.getPk(), contact);
        }

        return contactMap;
    }

    /**
     *
     * @return a list of the contact primary keys for every contact who is registered for interest in one or more genes
     */
    public List<Integer> getContactsWithRegistrations() {

        String contactPkQuery =
                "SELECT DISTINCT\n" +
                        "  c.pk\n" +
                        "FROM contact_gene cg\n" +
                        "JOIN contact      c  ON c.pk = cg.contact_pk\n" +
                        "ORDER BY c.pk";

        List<Integer> contactPks = jdbcInterest.queryForList(contactPkQuery, new HashMap<String, Object>(), Integer.class);

        return contactPks;
    }

    public Map<Integer, String> getEmailAddressesByContactGenePk() {
        Map<Integer, String> results = new HashMap<>();
        final String query =
                "SELECT cg.pk, c.address\n" +
                        "FROM contact c\n" +
                        "JOIN contact_gene cg ON cg.contact_pk      = c. pk\n" +
                        "JOIN gene_sent    gs ON gs.contact_gene_pk = cg.pk";

        List<Map<String, Object>> listMap = jdbcInterest.queryForList(query, new HashMap<>());
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
     * @return A map of all genes, indexed by mgi accession id.
     */
    public Map<String, Gene> getGenesByGeneAccessionId() {

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
     *
     * @return A map, indexed by contact primary key, of all genes assigned to each contact
     */
    public Map<Integer, List<Gene>> getGenesByContactPk() {

        Map<Integer, List<Gene>> genesByContactMap = new ConcurrentHashMap<>();

        String query =
                "SELECT DISTINCT\n" +
                        "  g.*\n" +
                        "FROM contact_gene cg\n" +
                        "JOIN gene         g  ON g.pk = cg.gene_pk\n" +
                        "WHERE cg.contact_pk = :contactPk\n" +
                        "ORDER BY g.symbol";

        Map<String, Object> parameterMap = new HashMap<>();

        List<Integer> contactPks = getContactsWithRegistrations();
        for (Integer contactPk : contactPks) {

            parameterMap.put("contactPk", contactPk);
            List<Gene> genes = jdbcInterest.query(query, parameterMap, new GeneRowMapper());
            genesByContactMap.put(contactPk, genes);
        }

        return genesByContactMap;
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
     *
     * @return A {@link Map} of all {@link GeneSent} instances, indexed by contact_gene_pk
     */
    public Map<Integer, GeneSent> getGeneSent() {

        Map<Integer, GeneSent> sentMap = new HashMap<>();

        final String query = "SELECT * FROM gene_sent";
        Map<String, Object> parameterMap = new HashMap<>();

        List<GeneSent> geneSentList = jdbcInterest.query(query, parameterMap, new GeneSentRowMapper());
        for (GeneSent geneSent : geneSentList) {
            sentMap.put(geneSent.getContactGenePk(), geneSent);
        }

        return sentMap;
    }

    /**
     * Return the {@link GeneSent}
     *
     * @param pk the gene_sent primary key
     *
     * @return the {@link GeneSent} matching the contactGenePk if found; null otherwise
     */
    public GeneSent getGeneSent(int pk) {

        final String query = "SELECT * FROM gene_sent WHERE pk = :pk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pk", pk);

        List<GeneSent> genesSent = jdbcInterest.query(query, parameterMap, new GeneSentRowMapper());

        return (genesSent.isEmpty() ? null : genesSent.get(0));
    }

    /**
     * Return a list of {@link GeneSent} matching the given {@code address}
     * @param address the contact e-mail address
     * @return the list of {@link GeneSent}
     */
    public List<GeneSent> getGeneSentForContact(String address) {

        final String query =
                "SELECT * FROM gene_sent gs\n" +
                        "JOIN contact_gene cg ON cg.pk = gs.contact_gene_pk\n" +
                        "JOIN contact c ON c.pk = cg.contact_pk\n" +
                        "WHERE c.address = :address";


        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("address", address);

        List<GeneSent> summaryList = jdbcInterest.query(query, parameterMap, new GeneSentRowMapper());

        return summaryList;
    }

    /**
     *
     * @return A {@link Map} of all {@link GeneSentSummary} instances, indexed by contact_pk
     */
    public Map<Integer, GeneSentSummary> getGeneSentSummary() {

        Map<Integer, GeneSentSummary> sentMap = new HashMap<>();

        final String query = "SELECT * FROM gene_sent_summary";
        Map<String, Object> parameterMap = new HashMap<>();

        List<GeneSentSummary> summaryList = jdbcInterest.query(query, parameterMap, new GeneSentSummaryRowMapper());
        for (GeneSentSummary summary : summaryList) {
            sentMap.put(summary.getContactPk(), summary);
        }

        return sentMap;
    }


    /**
     * Return the {@link GeneSentSummary} for the given {@code address}
     * @param address the contact e-mail address
     * @return the {@link GeneSentSummary} if found; null otherwise
     */
    public GeneSentSummary getGeneSentSummaryForContact(String address) {

        GeneSentSummary result = null;

        final String query =
                "SELECT * FROM gene_sent_summary gss\n" +
                        "JOIN contact c ON c.pk = gss.contact_pk\n" +
                        "WHERE c.address = :address";


        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("address", address);

        List<GeneSentSummary> summaryList = jdbcInterest.query(query, parameterMap, new GeneSentSummaryRowMapper());
        if (summaryList.size() > 0) {
            result = summaryList.get(0);
        }

        return result;
    }

    public int getGeneSentSummaryPendingEmailCount() {

        final String query = "SELECT COUNT(*) FROM gene_sent_summary WHERE sent_at IS NULL";
        Map<String, Object> parameterMap = new HashMap<>();

        return jdbcInterest.queryForObject(query, parameterMap, Integer.class);
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
                        "  GROUP_CONCAT(c.address, \"::\", cg.created_at ORDER BY cg.created_at SEPARATOR ' | ') AS subscribers\n" +
                        "FROM gene g\n" +
                        "JOIN contact_gene cg ON cg.gene_pk = g. pk\n" +
                        "JOIN contact      c  ON c. pk      = cg.contact_pk\n" +
                        "GROUP BY g.symbol\n" +
                        "ORDER BY count(address) DESC, g.symbol";

        Map<String, Object> parameterMap = new HashMap<>();

        List<GenesOfInterestByPopularity> genes = jdbcInterest.query(query, parameterMap, new GenesOfInterestByPopularityRowMapper());

        return genes;
    }

    public List<GeneSent> getGenesScheduledForSending() {

        final String query = "SELECT * FROM gene_sent WHERE sent_at IS NULL";
        Map<String, Object> parameterMap = new HashMap<>();

        List<GeneSent> genesSentList = jdbcInterest.query(query, parameterMap, new GeneSentRowMapper());

        return genesSentList;
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
     * @param token the token to match
     * @return the most recent {@link ResetCredentials} instance matching {@code token}, if found; null otherwise
     */
    public ResetCredentials getResetCredentials(String token) {

        String query =
                "SELECT * FROM reset_credentials WHERE token = :token";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("token", token);

        List<ResetCredentials> list = jdbcInterest.query(query, parameterMap, new ResetCredentialsRowMapper());

        return (list.isEmpty() ? null : list.get(0));
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
     *
     * @return A {@link Map} of {@link GeneStatus} instances, keyed by gene_status.pk
     */
    public Map<Integer, GeneStatus> getStatusMapByStatusPk() {

        Map<Integer, GeneStatus> statusMap = new HashMap<>();

        final String query = "SELECT * FROM gene_status";
        Map<String, Object> parameterMap = new HashMap<>();

        List<GeneStatus> geneStatusList = jdbcInterest.query(query, parameterMap, new GeneStatusRowMapper());
        for (GeneStatus geneStatus : geneStatusList) {
            statusMap.put(geneStatus.getPk(), geneStatus);
        }

        return statusMap;
    }

    public Summary getSummary(String emailAddress) {
        final String query =
                "SELECT g.* FROM gene g\n" +
                        "LEFT OUTER JOIN contact_gene cg ON cg.gene_pk = g.pk\n" +
                        "JOIN contact      c  ON c. pk      = cg.contact_pk\n" +
                        "WHERE c.address = :emailAddress\n";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", emailAddress);

        Summary summary = new Summary();
        summary.setEmailAddress(emailAddress);
        List<Gene> genes = jdbcInterest.query(query, parameterMap, new GeneRowMapper());
        summary.setGenes(genes);

        return summary;
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

    /**
     * Insert the given {@link GeneSentSummary} instance.
     *
     * @param geneSentSummary the {@link GeneSentSummary } instance to be used to update the database
     *                        @return the inserted {@link GeneSentSummary} primary key}
     *
     * @throws InterestException
     */
    public int insertGeneSentSummary(GeneSentSummary geneSentSummary) throws InterestException {

        String insert = "INSERT INTO gene_sent_summary (subject, body, contact_pk, created_at, sent_at)" +
                " VALUES (:subject, :body, :contactPk, :createdAt, :sentAt)";
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("subject", geneSentSummary.getSubject());
        parameterMap.put("body", geneSentSummary.getBody());
        parameterMap.put("contactPk", geneSentSummary.getContactPk());
        parameterMap.put("createdAt", geneSentSummary.getCreatedAt());
        parameterMap.put("sentAt", geneSentSummary.getSentAt());

        KeyHolder          keyholder       = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

        int count = jdbcInterest.update(insert, parameterSource, keyholder);
        if (count > 0) {
            return keyholder.getKey().intValue();
        }

        throw new InterestException("Unable to get primary key after INSERT.");
    }

    /**
     * Register {@code geneAccessionId} to {@code emailAddress}
     *
     * @param emailAddress contact email address
     * @param geneAccessionId gene accession id
     *
     * @throws InterestException (HttpRequest.INTERNAL_SERVER_ERROR) if either {@code emailAddress} or
     *                           {@code geneAccessionId} doesn't exist
     */
    public void registerGene(String emailAddress, String geneAccessionId) throws InterestException {

        String message;

        Gene gene = getGene(geneAccessionId);
        if (gene == null) {
            message = "Invalid gene " + geneAccessionId + ".";
            logger.error(message);
            throw new InterestException(message, HttpStatus.NOT_FOUND);
        }

        Contact contact = getContact(emailAddress);
        if (contact == null) {
            message = "Invalid contact " + emailAddress + ".";
            logger.error(message);
            throw new InterestException(message, HttpStatus.NOT_FOUND);
        }

        Date now = new Date();
        String insert = "INSERT INTO contact_gene (contact_pk, gene_pk, created_at) VALUES " +
                "(:contactPk, :genePk, :createdAt)";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("contactPk", contact.getPk());
        parameterMap.put("genePk", gene.getPk());
        parameterMap.put("createdAt", now);

        int rowCount;
        try {

            rowCount = jdbcInterest.update(insert, parameterMap);
            if (rowCount < 1) {
                message = "Unable to register gene " + geneAccessionId + " for contact " + emailAddress + ".";
                logger.error(message);
                throw new InterestException(message, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (DuplicateKeyException e) {
            throw e;
        }
    }

    public void truncateGeneSentSummary() {

        final String query = "DELETE FROM gene_sent_summary";
        jdbcInterest.getJdbcOperations().execute(query);
    }

    /**
     * Unregister {@code geneAccessionId} from {@code emailAddress}
     *
     * @param emailAddress contact email address
     * @param geneAccessionId gene accession id
     *
     * @throws InterestException (HttpRequest.INTERNAL_SERVER_ERROR) if either {@code emailAddress} or
     *                           {@code geneAccessionId} doesn't exist
     */
    public void unregisterGene(String emailAddress, String geneAccessionId) throws InterestException {

        String message;

        Gene gene = getGene(geneAccessionId);
        if (gene == null) {
            message = "Invalid gene " + geneAccessionId + ".";
            logger.error(message);
            throw new InterestException(message, HttpStatus.NOT_FOUND);
        }

        Contact contact = getContact(emailAddress);
        if (contact == null) {
            message = "Invalid contact " + emailAddress + ".";
            logger.error(message);
            throw new InterestException(message, HttpStatus.NOT_FOUND);
        }

        String delete = "DELETE FROM contact_gene WHERE contact_pk = :contactPk AND gene_pk = :genePk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("contactPk", contact.getPk());
        parameterMap.put("genePk", gene.getPk());

        int rowCount;
//        try {

            rowCount = jdbcInterest.update(delete, parameterMap);
            if (rowCount < 1) {
                message = "Unable to unregister gene " + geneAccessionId + " for contact " + emailAddress + ".";
                logger.error(message);
                throw new InterestException(message, HttpStatus.INTERNAL_SERVER_ERROR);
            }

//        } catch (Exception e) {
//
//            message = "Exception while trying to unregister gene " + geneAccessionId + " for contact " + emailAddress + ": " + e.getLocalizedMessage();
//            logger.error(message);
//            throw new InterestException(message, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
    }

    /**
     * Update the account_locked flag to the supplied value
     * @param emailAddress contact email address
     * @param accountLocked new value
     * @throws InterestException (HttpRequest.INTERNAL_SERVER_ERROR) if {@code emailAddress} doesn't exist
     */
    public void updateAccountLocked(String emailAddress, boolean accountLocked) throws InterestException {

        String message;
        String update = "UPDATE contact SET account_locked = :accountLocked WHERE address = :address";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("accountLocked", accountLocked ? 1 : 0);
        parameterMap.put("address", emailAddress);

        int rowCount = jdbcInterest.update(update, parameterMap);
        if (rowCount < 1) {
            message = "Unable to update password for contact " + emailAddress + ".";
            logger.error(message);
            throw new InterestException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates all of the gene_sent.sent_to columns for the given contact e-mail {@code address} with the given {@link Date}.
     *
     * @param address the contact e-mail filter
     * @param date the {@link Date } to set gene_sent.sent_at to
     *
     * @throws InterestException
     */
    public void updateGeneSentDates(String address, Date date) throws InterestException {

        String update = "UPDATE gene_sent SET sent_at = :date WHERE pk = :pk";

        // NOTE: We cannot use the UPDATE ... JOIN ... SET mysql syntax because H2 (used for testing) doesn't support it.
        List<GeneSent> genesSent = getGeneSentForContact(address);

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("date", date);

        for (GeneSent geneSent : genesSent) {
            parameterMap.put("pk", geneSent.getPk());
            jdbcInterest.update(update, parameterMap);
        }
    }

    /**
     * Update the given {@link GeneSentSummary} instance.
     *
     * @param geneSentSummary the {@link GeneSentSummary } instance to be used to update the database
     *                        @return the inserted {@link GeneSentSummary} primary key}
     *
     * @throws InterestException
     */
    public void updateGeneSentSummary(GeneSentSummary geneSentSummary) throws InterestException {

        String update = "UPDATE gene_sent_summary SET subject = :subject, body = :body, contact_pk = :contactPk, created_at = :createdAt, sent_at = :sentAt WHERE pk = :pk";
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pk", geneSentSummary.getPk());
        parameterMap.put("subject", geneSentSummary.getSubject());
        parameterMap.put("body", geneSentSummary.getBody());
        parameterMap.put("contactPk", geneSentSummary.getContactPk());
        parameterMap.put("createdAt", geneSentSummary.getCreatedAt());
        parameterMap.put("sentAt", geneSentSummary.getSentAt());

        jdbcInterest.update(update, parameterMap);
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
     * Update the encrypted password for {@code emailAddress}
     *
     * @param emailAddress contact email address
     * @param encryptedPassword new, encrypted password
     * @throws InterestException (HttpRequest.INTERNAL_SERVER_ERROR) if {@code emailAddress} doesn't exist
     */
    public void updatePassword(String emailAddress, String encryptedPassword) throws InterestException {

        String message;
        String update = "UPDATE contact SET password = :password, password_expired = 0 WHERE address = :address";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("password", encryptedPassword);
        parameterMap.put("address", emailAddress);

        int rowCount = jdbcInterest.update(update, parameterMap);
        if (rowCount < 1) {
            message = "Unable to update password for contact " + emailAddress + ".";
            logger.error(message);
            throw new InterestException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update the password_expired flag to the supplied value
     *
     * @param emailAddress contact email address
     * @param passwordExpired new value
     * @throws InterestException (HttpRequest.INTERNAL_SERVER_ERROR)
     */
    public void updatePasswordExpired(String emailAddress, boolean passwordExpired) throws InterestException {

        String message;
        String update = "UPDATE contact SET password_expired = :passwordExpired WHERE address = :address";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("passwordExpired", passwordExpired ? 1 : 0);
        parameterMap.put("address", emailAddress);

        int rowCount = jdbcInterest.update(update, parameterMap);
        if (rowCount < 1) {
            message = "Unable to update passwordExpired for contact " + emailAddress + ".";
            logger.error(message);
            throw new InterestException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Try to INSERT the {@link ResetCredentials} instance. If the emailAddress already exists, update the instance.
     * @param resetCredentials the instance to INSERT or UPDATE.
     */
    public void updateResetCredentials(ResetCredentials resetCredentials) {

        String insert = "INSERT INTO reset_credentials (email_address, token, created_at) VALUES " +
                        "(:emailAddress, :token, :createdAt)";

        String update = "UPDATE reset_credentials SET token = :token, created_at = :createdAt " +
                        "WHERE email_address = :emailAddress";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", resetCredentials.getAddress());
        parameterMap.put("token", resetCredentials.getToken());
        parameterMap.put("createdAt", resetCredentials.getCreatedAt());

        try {

            jdbcInterest.update(insert, parameterMap);

        } catch (DuplicateKeyException e) {

            jdbcInterest.update(update, parameterMap);
        }
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

    private int insertGeneSent(SqlParameterSource parameterSource, KeyHolder keyholder) throws InterestException {

        // Try to insert the row into gene_sent. If the contact_gene_pk already exists, the INSERT operation is ignored.
        final String columnNames =
                "subject, body, contact_gene_pk, " +
                        "assignment_status_pk, conditional_allele_production_status_pk, null_allele_production_status_pk, phenotyping_status_pk, " +
                        "created_at, sent_at";

        final String columnValues =
                ":subject, :body, :contact_gene_pk, " +
                        ":assignment_status_pk, :conditional_allele_production_status_pk, :null_allele_production_status_pk, :phenotyping_status_pk, " +
                        ":created_at, :sent_at";

        final String query = "INSERT INTO gene_sent(" + columnNames + ") VALUES (" + columnValues + ")";

        int count = jdbcInterest.update(query, parameterSource, keyholder);
        if (count > 0) {
            return keyholder.getKey().intValue();
        }

        throw new InterestException("Unable to get primary key after INSERT.");
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

    private Map<String, Object> loadSentParameterMap(GeneSent geneSent) {

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("subject", geneSent.getSubject());
        parameterMap.put("body", geneSent.getBody());
        parameterMap.put("contact_gene_pk", geneSent.getContactGenePk());

        parameterMap.put("assignment_status_pk", geneSent.getAssignmentStatusPk());
        parameterMap.put("conditional_allele_production_status_pk", geneSent.getConditionalAlleleProductionStatusPk());
        parameterMap.put("null_allele_production_status_pk", geneSent.getNullAlleleProductionStatusPk());
        parameterMap.put("phenotyping_status_pk", geneSent.getPhenotypingStatusPk());

        parameterMap.put("created_at", (geneSent.getCreatedAt() == null ? new Date() : geneSent.getCreatedAt()));
        parameterMap.put("sent_at", geneSent.getSentAt());

        return parameterMap;
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

    private int updateGeneSent(GeneSent geneSent, Map<String, Object> parameterMap) {

        final String colData =
                // Omit contact_gene_pk in the UPDATE as it is used in the WHERE clause.

                "subject = :subject, " +
                "body = :body, " +
                "contact_gene_pk = :contact_gene_pk, " +

                "assignment_status_pk = :assignment_status_pk, " +
                "conditional_allele_production_status_pk = :conditional_allele_production_status_pk, " +
                "null_allele_production_status_pk = :null_allele_production_status_pk, " +
                "phenotyping_status_pk = :phenotyping_status_pk, " +

                "sent_at = :sent_at";

        String query = "UPDATE gene_sent SET " + colData + " WHERE contact_gene_pk = :contact_gene_pk";

        int count = jdbcInterest.update(query, parameterMap);
        if (count > 0) {

            // Get and update the gene_sent primary key.
            query = "SELECT pk FROM gene_sent WHERE contact_gene_pk = :contact_gene_pk";
            int pk = jdbcInterest.queryForObject(query, parameterMap, Integer.class);
            geneSent.setPk(pk);
        }

        return count;
    }







    // KNOWN DEPRECATED METHODS

    /**
     * Activate interest described by {@code gene} and {@code email}.
     *
     * @param invoker The authorised invoker of this request
     * @param geneAccessionId The mgi accession id of the gene being registgered
     * @param emailAddress The email address being registered
     *
     * @return The primary key of the newly inserted contact_gene row.
     *
     *         @throws InterestException if the gene does not exist
     */
    @Deprecated
    public int insertOrUpdateInterestGene(String invoker, String geneAccessionId, String emailAddress) throws InterestException {

        String message;
        SecurityUtils securityUtils = new SecurityUtils();

        // Check that the gene exists.
        Gene gene = getGene(geneAccessionId);
        if (gene == null) {
            message = "Register contact " + emailAddress + " for gene " + geneAccessionId + " failed: No such gene";
            logger.error(message);
            throw new InterestException(message, HttpStatus.NOT_FOUND);
        }

        Contact contact = getContact(emailAddress);
        if (contact == null) {

            // Insert new contact
            createAccount(emailAddress, securityUtils.generateSecureRandomPassword());
            contact = getContact(emailAddress);
        }

        // Register the gene.
        registerGene(emailAddress, gene.getMgiAccessionId());

        return getContactGenePrimaryKey(emailAddress, geneAccessionId);
    }

    // FIXME Needed only for ApplicationMigrateContactGeneSent.
    @Deprecated
    public int getContactGenePrimaryKey(String emailAddress, String geneAccessionId) {
        Contact contact = getContact(emailAddress);
        Gene gene = getGene(geneAccessionId);

        final String query = "SELECT pk FROM contact_gene WHERE contact_pk = :contactPk AND gene_pk = :genePk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("contactPk", contact.getPk());
        parameterMap.put("genePk", gene.getPk());

        int pk  = jdbcInterest.queryForObject(query, parameterMap, Integer.class);

        return pk;
    }
}