SET AUTOCOMMIT = 0;
SET FOREIGN_KEY_CHECKS = 0;


DROP TABLE IF EXISTS contact;
CREATE TABLE contact (
    pk            INT          NOT NULL         AUTO_INCREMENT PRIMARY KEY,
    address       VARCHAR(255) NOT NULL UNIQUE,
    active        INT          NOT NULL         DEFAULT 1,                   -- 1 = active; 0 = inactive

    created_at    DATETIME     NOT NULL,
    updated_at    TIMESTAMP    NOT NULL         DEFAULT CURRENT_TIMESTAMP
                    ON UPDATE CURRENT_TIMESTAMP

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS gene_contact;
CREATE TABLE gene_contact (
    pk             INT          NOT NULL      AUTO_INCREMENT PRIMARY KEY,
    contact_pk     INT          NOT NULL,
    gene_pk        INT          NOT NULL,

    created_at     DATETIME     NOT NULL,
    updated_at     TIMESTAMP    NOT NULL        DEFAULT CURRENT_TIMESTAMP
                     ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY contact_pk_fk   (contact_pk) REFERENCES contact(pk),
    FOREIGN KEY gene_pk_fk      (gene_pk)    REFERENCES gene(pk),
    UNIQUE KEY  gene_contact_uk (contact_pk, gene_pk)

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS gene_status;
CREATE TABLE gene_status (
    pk          INT          NOT NULL           AUTO_INCREMENT PRIMARY KEY,
    status      VARCHAR(64)  NOT NULL UNIQUE,
    active      INT          NOT NULL           DEFAULT 1,                   -- 1 = active; 0 = inactive

    created_at  DATETIME     NOT NULL,
    updated_at  TIMESTAMP    NOT NULL            DEFAULT CURRENT_TIMESTAMP
                  ON UPDATE CURRENT_TIMESTAMP

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS imits_status;
CREATE TABLE imits_status (
    pk              INT          NOT NULL           AUTO_INCREMENT PRIMARY KEY,
    gene_status_pk  INT                             DEFAULT NULL,
    status          VARCHAR(64)  NOT NULL UNIQUE,
    active          INT          NOT NULL           DEFAULT 1,                   -- 1 = active; 0 = inactive

    created_at      DATETIME     NOT NULL,
    updated_at      TIMESTAMP    NOT NULL           DEFAULT CURRENT_TIMESTAMP
                      ON UPDATE CURRENT_TIMESTAMP

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS gene;
CREATE TABLE gene (
    pk                                                          INT          NOT NULL  AUTO_INCREMENT PRIMARY KEY,

    mgi_accession_id                                            VARCHAR(32)  NOT NULL           UNIQUE,
    symbol                                                      VARCHAR(128) NOT NULL,
    assigned_to                                                 VARCHAR(128) DEFAULT NULL,
    assignment_status                                           VARCHAR(128) DEFAULT NULL,
    assignment_status_date                                      DATETIME     DEFAULT NULL,
    assignment_status_pk                                        INT          DEFAULT NULL,

    conditional_allele_production_centre                        VARCHAR(128) DEFAULT NULL,
    conditional_allele_production_status                        VARCHAR(128) DEFAULT NULL,
    conditional_allele_production_status_date                   DATETIME     DEFAULT NULL,
    conditional_allele_production_status_pk                     INT          DEFAULT NULL,

    null_allele_production_centre                               VARCHAR(128) DEFAULT NULL,
    null_allele_production_status                               VARCHAR(128) DEFAULT NULL,
    null_allele_production_status_date                          DATETIME     DEFAULT NULL,
    null_allele_production_status_pk                            INT          DEFAULT NULL,

    phenotyping_centre                                          VARCHAR(128) DEFAULT NULL,
    phenotyping_status                                          VARCHAR(128) DEFAULT NULL,
    phenotyping_status_date                                     DATETIME     DEFAULT NULL,
    phenotyping_status_pk                                       INT          DEFAULT NULL,

    number_of_significant_phenotypes                            INT          DEFAULT 0,

    created_at                                                  DATETIME     NOT NULL,
    updated_at                                                  TIMESTAMP    NOT NULL   DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY assignment_status_pk_fk                         (assignment_status_pk)                      REFERENCES gene_status(pk),
    FOREIGN KEY conditional_allele_production_status_pk_fk      (conditional_allele_production_status_pk)   REFERENCES gene_status(pk),
    FOREIGN KEY null_allele_production_status_pk_fk             (null_allele_production_status_pk)          REFERENCES gene_status(pk),
    FOREIGN KEY phenotyping_status_pk_fk                        (phenotyping_status_pk)                     REFERENCES gene_status(pk)

) COLLATE=utf8_general_ci ENGINE=InnoDb;

DROP TABLE IF EXISTS gene_sent;
CREATE TABLE gene_sent (
    pk                                          INT             NOT NULL        AUTO_INCREMENT PRIMARY KEY,
    subject                                     VARCHAR(78)     NOT NULL,
    body                                        VARCHAR(2048)   NOT NULL,
    gene_contact_pk                             INT             NOT NULL,
    assignment_status_pk                        INT             NOT NULL,
    conditional_allele_production_status_pk     INT             NOT NULL,
    null_allele_production_status_pk            INT             NOT NULL,
    phenotyping_status_pk                       INT             NOT NULL,

    created_at                                  DATETIME        NOT NULL,
    sent_at                                     DATETIME,                       -- a null value means 'generated but not geneSent yet'.
    updated_at                                  TIMESTAMP       NOT NULL        DEFAULT CURRENT_TIMESTAMP
                                                  ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY gene_contact_pk_fk                              (gene_contact_pk)                               REFERENCES gene_contact(pk),
    FOREIGN KEY assignment_status_pk_fk                         (assignment_status_pk)                          REFERENCES gene_status(pk),
    FOREIGN KEY conditional_allele_production_status_pk_fk      (conditional_allele_production_status_pk)       REFERENCES gene_status(pk),
    FOREIGN KEY null_allele_production_status_pk_fk             (null_allele_production_status_pk)              REFERENCES gene_status(pk),
    FOREIGN KEY phenotyping_status_pk_fk                        (phenotyping_status_pk)                         REFERENCES gene_status(pk),
    UNIQUE KEY  gene_contact_pk_uk                              (gene_contact_pk)

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS log;
CREATE TABLE log (
    pk                      INT            NOT NULL      AUTO_INCREMENT PRIMARY KEY,
    contact_pk              INT            NOT NULL,
    gene_status_pk          INT,
    imits_status_pk         INT,
    gene_pk                 INT,
    gene_sent_pk            INT,
    message                 VARCHAR(2048)  NOT NULL,

    updated_at              TIMESTAMP      NOT NULL      DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY contact_pk_fk           (contact_pk)        REFERENCES contact(pk),
    FOREIGN KEY gene_status_pk_fk       (gene_status_pk)    REFERENCES gene_status(pk),
    FOREIGN KEY imits_status_pk_fk      (imits_status_pk)   REFERENCES imits_status(pk),
    FOREIGN KEY gene_pk_fk              (gene_pk)           REFERENCES gene(pk),
    FOREIGN KEY gene_sent_pk_fk         (gene_sent_pk)      REFERENCES gene_sent(pk)

) COLLATE=utf8_general_ci ENGINE=InnoDb;


-- POPULATE STATIC TABLES

SET @now = NOW();

INSERT INTO gene_status (status, created_at) VALUES
    ('more_phenotyping_data_available', @now),
    ('mouse_produced', @now),
    ('mouse_production_started', @now),
    ('not_planned', @now),
    ('phenotyping_data_available', @now),
    ('production_and_phenotyping_planned', @now),
    ('register', @now),
    ('unregister', @now),
    ('withdrawn', @now);

INSERT INTO imits_status (status, gene_status_pk, created_at) VALUES
    ('Aborted - ES Cell QC Failed', (SELECT pk FROM gene_status WHERE status = 'production_and_phenotyping_planned'), @now),
    ('Assigned - ES Cell QC Complete', (SELECT pk FROM gene_status WHERE status = 'production_and_phenotyping_planned'), @now),
    ('Assigned - ES Cell QC In Progress', (SELECT pk FROM gene_status WHERE status = 'production_and_phenotyping_planned'), @now),
    ('Assigned', (SELECT pk FROM gene_status WHERE status = 'production_and_phenotyping_planned'), @now),
    ('Chimeras obtained', (SELECT pk FROM gene_status WHERE status = 'mouse_production_started'), @now),
    ('Chimeras/Founder obtained', (SELECT pk FROM gene_status WHERE status = 'mouse_production_started'), @now),
    ('Conflict', (SELECT pk FROM gene_status WHERE status = 'production_and_phenotyping_planned'), @now),
    ('Cre Excision Complete', (SELECT pk FROM gene_status WHERE status = 'mouse_produced'), @now),
    ('Cre Excision Started', (SELECT pk FROM gene_status WHERE status = 'mouse_production_started'), @now),
    ('Founder obtained', (SELECT pk FROM gene_status WHERE status = 'mouse_production_started'), @now),
    ('Genotype confirmed', (SELECT pk FROM gene_status WHERE status = 'mouse_produced'), @now),
    ('Inactive', (SELECT pk FROM gene_status WHERE status = 'withdrawn'), @now),
    ('Inspect - Conflict', (SELECT pk FROM gene_status WHERE status = 'production_and_phenotyping_planned'), @now),
    ('Inspect - GLT Mouse', (SELECT pk FROM gene_status WHERE status = 'production_and_phenotyping_planned'), @now),
    ('Inspect - MI Attempt', (SELECT pk FROM gene_status WHERE status = 'production_and_phenotyping_planned'), @now),
    ('Interest', (SELECT pk FROM gene_status WHERE status = 'production_and_phenotyping_planned'), @now),
    ('Micro-injection aborted', (SELECT pk FROM gene_status WHERE status = 'mouse_production_started'), @now),
    ('Micro-injection in progress', (SELECT pk FROM gene_status WHERE status = 'mouse_production_started'), @now),
    ('Mouse Allele Modification Registered', (SELECT pk FROM gene_status WHERE status = 'mouse_production_started'), @now),
    ('Phenotype Attempt Registered', NULL, @now),
    ('Phenotype Production Aborted', NULL, @now),
    ('Phenotyping Complete', (SELECT pk FROM gene_status WHERE status = 'phenotyping_data_available'), @now),
    ('Phenotyping Production Registered', NULL, @now),
    ('Phenotyping Started', NULL, @now),
    ('Rederivation Complete', (SELECT pk FROM gene_status WHERE status = 'mouse_production_started'), @now),
    ('Rederivation Started', (SELECT pk FROM gene_status WHERE status = 'mouse_production_started'), @now),
    ('Withdrawn', (SELECT pk FROM gene_status WHERE status = 'withdrawn'), @now);

SET AUTOCOMMIT = 1;
SET FOREIGN_KEY_CHECKS = 1;