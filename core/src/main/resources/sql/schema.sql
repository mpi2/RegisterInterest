SET AUTOCOMMIT = 0;
SET FOREIGN_KEY_CHECKS = 0;


DROP TABLE IF EXISTS contact;
CREATE TABLE contact (
    pk                INT          NOT NULL         AUTO_INCREMENT PRIMARY KEY,

    address           VARCHAR(255) NOT NULL UNIQUE,
    password          VARCHAR(256) NOT NULL         DEFAULT '',
    password_expired  INT          NOT NULL         DEFAULT 1,                          -- 1 = expired; 0 = not expired
    account_locked    INT          NOT NULL         DEFAULT 0,                          -- 1 = locked; 0 = not locked

    created_at        DATETIME     NOT NULL,
    updated_at        TIMESTAMP    NOT NULL         DEFAULT CURRENT_TIMESTAMP
                        ON UPDATE CURRENT_TIMESTAMP

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS gene_contact;
DROP TABLE IF EXISTS contact_gene;
CREATE TABLE contact_gene (
    pk             INT          NOT NULL      AUTO_INCREMENT PRIMARY KEY,

    contact_pk     INT          NOT NULL,
    gene_pk        INT          NOT NULL,

    created_at     DATETIME     NOT NULL,
    updated_at     TIMESTAMP    NOT NULL        DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY contact_pk_fk   (contact_pk) REFERENCES contact(pk),
    FOREIGN KEY gene_pk_fk      (gene_pk)    REFERENCES gene(pk),
    UNIQUE KEY  contact_gene_uk (contact_pk, gene_pk)

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS contact_role;
CREATE TABLE contact_role (
    pk             INT          NOT NULL      AUTO_INCREMENT PRIMARY KEY,

    contact_pk     INT          NOT NULL,
    role           VARCHAR(64)  NOT NULL      DEFAULT 'USER',

    created_at     DATETIME     NOT NULL,
    updated_at     TIMESTAMP    NOT NULL      DEFAULT CURRENT_TIMESTAMP
                     ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY contact_pk_fk   (contact_pk) REFERENCES contact(pk),
    UNIQUE KEY  contact_role_uk (contact_pk, role)

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS gene;
CREATE TABLE gene (
    pk                                                          INT          NOT NULL  AUTO_INCREMENT PRIMARY KEY,

    mgi_accession_id                                            VARCHAR(32)  NOT NULL           UNIQUE,
    symbol                                                      VARCHAR(128) NOT NULL,
    assigned_to                                                 VARCHAR(128) DEFAULT NULL,
    assignment_status                                           VARCHAR(128) DEFAULT NULL,
    assignment_status_date                                      DATETIME     DEFAULT NULL,
    ri_assignment_status                                        VARCHAR(64)  DEFAULT NULL,

    conditional_allele_production_centre                        VARCHAR(128) DEFAULT NULL,
    conditional_allele_production_status                        VARCHAR(128) DEFAULT NULL,
    ri_conditional_allele_production_status                     VARCHAR(64)  DEFAULT NULL,
    conditional_allele_production_status_date                   DATETIME     DEFAULT NULL,
    conditional_allele_production_start_date                    DATETIME     DEFAULT NULL,

    null_allele_production_centre                               VARCHAR(128) DEFAULT NULL,
    null_allele_production_status                               VARCHAR(128) DEFAULT NULL,
    ri_null_allele_production_status                            VARCHAR(64)  DEFAULT NULL,
    null_allele_production_status_date                          DATETIME     DEFAULT NULL,
    null_allele_production_start_date                           DATETIME     DEFAULT NULL,

    phenotyping_centre                                          VARCHAR(128) DEFAULT NULL,
    phenotyping_status                                          VARCHAR(128) DEFAULT NULL,
    phenotyping_status_date                                     DATETIME     DEFAULT NULL,
    ri_phenotyping_status                                       VARCHAR(64)  DEFAULT NULL,

    number_of_significant_phenotypes                            INT          DEFAULT 0,

    created_at                                                  DATETIME     NOT NULL,
    updated_at                                                  TIMESTAMP    NOT NULL   DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY ri_assignment_status_fk                         (ri_assignment_status)                      REFERENCES gene_status(status),
    FOREIGN KEY ri_conditional_allele_production_status_fk      (ri_conditional_allele_production_status)   REFERENCES gene_status(status),
    FOREIGN KEY ri_null_allele_production_status_fk             (ri_null_allele_production_status)          REFERENCES gene_status(status),
    FOREIGN KEY ri_phenotyping_status_fk                        (ri_phenotyping_status)                     REFERENCES gene_status(status)

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS gene_sent;
CREATE TABLE gene_sent (
    pk                                          INT             NOT NULL        AUTO_INCREMENT PRIMARY KEY,

    address                                     VARCHAR(255)    NOT NULL,
    mgi_accession_id                            VARCHAR(32)     NOT NULL,
    assignment_status                           VARCHAR(64)     DEFAULT NULL,
    conditional_allele_production_status        VARCHAR(64)     DEFAULT NULL,
    null_allele_production_status               VARCHAR(64)     DEFAULT NULL,
    phenotyping_status                          VARCHAR(64)     DEFAULT NULL,

    created_at                                  DATETIME        NOT NULL,
    sent_at                                     DATETIME,                       -- a null value means 'generated but not geneSent yet'.
    updated_at                                  TIMESTAMP       NOT NULL        DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY assignment_status_fk                         (assignment_status)                          REFERENCES gene_status(status),
    FOREIGN KEY conditional_allele_production_status_fk      (conditional_allele_production_status)       REFERENCES gene_status(status),
    FOREIGN KEY null_allele_production_status_fk             (null_allele_production_status)              REFERENCES gene_status(status),
    FOREIGN KEY phenotyping_status_fk                        (phenotyping_status)                         REFERENCES gene_status(status)

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS gene_sent_summary;
CREATE TABLE gene_sent_summary (
    pk                                          INT             NOT NULL        AUTO_INCREMENT PRIMARY KEY,

    subject                                     VARCHAR(78)     NOT NULL,
    body                                        MEDIUMTEXT      NOT NULL,
    address                                     VARCHAR(255)    NOT NULL,

    created_at                                  DATETIME        NOT NULL,
    sent_at                                     DATETIME,                       -- a null value means 'generated but not geneSent yet'.
    updated_at                                  TIMESTAMP       NOT NULL        DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS gene_status;
CREATE TABLE gene_status (
    pk          INT          NOT NULL           AUTO_INCREMENT PRIMARY KEY,

    status      VARCHAR(64)  NOT NULL UNIQUE,

    created_at  DATETIME     NOT NULL,
    updated_at  TIMESTAMP    NOT NULL           DEFAULT CURRENT_TIMESTAMP
                  ON UPDATE CURRENT_TIMESTAMP

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS imits_status;
CREATE TABLE imits_status (
    pk              INT          NOT NULL           AUTO_INCREMENT PRIMARY KEY,

    gene_status_pk  INT                             DEFAULT NULL,
    status          VARCHAR(64)  NOT NULL UNIQUE,

    created_at      DATETIME     NOT NULL,
    updated_at      TIMESTAMP    NOT NULL           DEFAULT CURRENT_TIMESTAMP
                      ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY gene_status_pk_fk   (gene_status_pk) REFERENCES gene_status(pk)

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS log;


DROP TABLE IF EXISTS reset_credentials;
CREATE TABLE reset_credentials (
    email_address  VARCHAR(255) NOT NULL PRIMARY KEY,
    token          TEXT         NOT NULL,
    created_at     DATETIME     NOT NULL

) COLLATE=utf8_general_ci ENGINE=InnoDb;


-- POPULATE STATIC TABLES

SET @now = NOW();

INSERT INTO gene_status (status, created_at) VALUES
    ('More phenotyping data available', @now),
    ('Genotype confirmed mice', @now),
    ('Started', @now),
    ('Not planned', @now),
    ('Phenotyping data available', @now),
    ('Selected for production and phenotyping', @now),
    ('register', @now),
    ('unregister', @now),
    ('Withdrawn', @now);

INSERT INTO imits_status (status, gene_status_pk, created_at) VALUES
    ('Aborted - ES Cell QC Failed', (SELECT pk FROM gene_status WHERE status = 'Selected for production and phenotyping'), @now),
    ('Assigned - ES Cell QC Complete', (SELECT pk FROM gene_status WHERE status = 'Selected for production and phenotyping'), @now),
    ('Assigned - ES Cell QC In Progress', (SELECT pk FROM gene_status WHERE status = 'Selected for production and phenotyping'), @now),
    ('Assigned', (SELECT pk FROM gene_status WHERE status = 'Selected for production and phenotyping'), @now),
    ('Assigned for phenotyping', (SELECT pk FROM gene_status WHERE status = 'Selected for production and phenotyping'), @now),
    ('Chimeras obtained', (SELECT pk FROM gene_status WHERE status = 'Started'), @now),
    ('Chimeras/Founder obtained', (SELECT pk FROM gene_status WHERE status = 'Started'), @now),
    ('Conflict', (SELECT pk FROM gene_status WHERE status = 'Selected for production and phenotyping'), @now),
    ('Cre Excision Complete', (SELECT pk FROM gene_status WHERE status = 'Genotype confirmed mice'), @now),
    ('Cre Excision Started', (SELECT pk FROM gene_status WHERE status = 'Started'), @now),
    ('Founder obtained', (SELECT pk FROM gene_status WHERE status = 'Started'), @now),
    ('Genotype confirmed', (SELECT pk FROM gene_status WHERE status = 'Genotype confirmed mice'), @now),
    ('Inactive', (SELECT pk FROM gene_status WHERE status = 'Withdrawn'), @now),
    ('Inspect - Conflict', (SELECT pk FROM gene_status WHERE status = 'Selected for production and phenotyping'), @now),
    ('Inspect - GLT Mouse', (SELECT pk FROM gene_status WHERE status = 'Selected for production and phenotyping'), @now),
    ('Inspect - MI Attempt', (SELECT pk FROM gene_status WHERE status = 'Selected for production and phenotyping'), @now),
    ('Interest', (SELECT pk FROM gene_status WHERE status = 'Selected for production and phenotyping'), @now),
    ('Micro-injection aborted', (SELECT pk FROM gene_status WHERE status = 'Started'), @now),
    ('Micro-injection in progress', (SELECT pk FROM gene_status WHERE status = 'Started'), @now),
    ('Mouse Allele Modification Registered', (SELECT pk FROM gene_status WHERE status = 'Started'), @now),
    ('Phenotype Attempt Registered', NULL, @now),
    ('Phenotype Production Aborted', NULL, @now),
    ('Phenotyping Complete', (SELECT pk FROM gene_status WHERE status = 'Phenotyping data available'), @now),
    ('Phenotyping Production Registered', NULL, @now),
    ('Phenotyping Started', NULL, @now),
    ('Rederivation Complete', (SELECT pk FROM gene_status WHERE status = 'Started'), @now),
    ('Rederivation Started', (SELECT pk FROM gene_status WHERE status = 'Started'), @now),
    ('Withdrawn', (SELECT pk FROM gene_status WHERE status = 'Withdrawn'), @now);

SET AUTOCOMMIT = 1;
SET FOREIGN_KEY_CHECKS = 1;