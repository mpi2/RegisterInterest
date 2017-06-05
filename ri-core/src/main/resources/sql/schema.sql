SET AUTOCOMMIT = 0;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS component;
CREATE TABLE component (
    pk          INT         NOT NULL        AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(64) NOT NULL UNIQUE,
    updated_at  TIMESTAMP   NOT NULL        DEFAULT CURRENT_TIMESTAMP
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

    updated_at                                                  TIMESTAMP    NOT NULL   DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    KEY         mgi_accession_id_idx                            (mgi_accession_id),
    KEY         updated_at_idx                                  (updated_at)
        /*
            FOREIGN KEY assignment_status_pk_fk                         (assignment_status_pk)                  REFERENCES status(pk),
            FOREIGN KEY conditional_allele_production_status_pk_fk      (conditional_allele_production_status)  REFERENCES status(pk),
            FOREIGN KEY null_allele_production_status_pk_fk             (null_allele_production_status)         REFERENCES status(pk),
            FOREIGN KEY phenotyping_status_pk_fk                        (phenotyping_status_pk)                 REFERENCES status(pk)
        */

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS contact;
CREATE TABLE contact (
    pk            INT          NOT NULL         AUTO_INCREMENT PRIMARY KEY,
    address       VARCHAR(255) NOT NULL UNIQUE,
    active        INT          NOT NULL         DEFAULT 1,                   -- 1 = active; 0 = inactive
    updated_at    TIMESTAMP   NOT NULL        DEFAULT CURRENT_TIMESTAMP
                    ON UPDATE CURRENT_TIMESTAMP

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS contact_gene;
CREATE TABLE contact_gene (
    pk             INT            NOT NULL      AUTO_INCREMENT PRIMARY KEY,
    contact_pk     INT            NOT NULL,
    gene_pk        INT            NOT NULL,
    updated_at     TIMESTAMP   NOT NULL        DEFAULT CURRENT_TIMESTAMP
                     ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY contact_pk_fk   (contact_pk) REFERENCES contact(pk),
    FOREIGN KEY gene_pk_fk      (gene_pk)    REFERENCES gene(pk),
    UNIQUE KEY  contact_gene_uk (contact_pk, gene_pk)

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS status;
CREATE TABLE status (
    pk          INT          NOT NULL           AUTO_INCREMENT PRIMARY KEY,
    status      VARCHAR(64)  NOT NULL UNIQUE,
    active      INT          NOT NULL           DEFAULT 1,                   -- 1 = active; 0 = inactive
    updated_at  TIMESTAMP    NOT NULL            DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS imits_status;
CREATE TABLE imits_status (
    pk          INT          NOT NULL           AUTO_INCREMENT PRIMARY KEY,
    status_pk   INT                             DEFAULT NULL,
    status      VARCHAR(64)  NOT NULL UNIQUE,
    active      INT          NOT NULL           DEFAULT 1,                   -- 1 = active; 0 = inactive
    updated_at  TIMESTAMP   NOT NULL            DEFAULT CURRENT_TIMESTAMP
                  ON UPDATE CURRENT_TIMESTAMP

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS sent;
CREATE TABLE sent (
    pk               INT            NOT NULL      AUTO_INCREMENT PRIMARY KEY,
    subject          VARCHAR(78)    NOT NULL,
    body             VARCHAR(2048)  NOT NULL,
    contact_pk       INT NOT NULL,
    component_pk     INT NOT NULL,
    status_pk        INT NOT NULL,
    gene_pk          INT,
    updated_at       TIMESTAMP   NOT NULL        DEFAULT CURRENT_TIMESTAMP
                        ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY contact_pk_fk     (contact_pk)   REFERENCES contact(pk),
    FOREIGN KEY component_pk_fk   (component_pk) REFERENCES component(pk),
    FOREIGN KEY status_pk_fk      (status_pk)    REFERENCES status(pk),
    FOREIGN KEY gene_pk_fk        (gene_pk)      REFERENCES gene(pk)

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS log;
CREATE TABLE log (
    pk                INT            NOT NULL      AUTO_INCREMENT PRIMARY KEY,
    contact_pk        INT            NOT NULL,
    component_pk      INT,
    status_pk         INT,
    imits_status_pk   INT,
    gene_pk           INT,
    sent_pk           INT,
    message           VARCHAR(2048)  NOT NULL,
    updated_at        TIMESTAMP   NOT NULL        DEFAULT CURRENT_TIMESTAMP
                         ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY contact_pk_fk           (contact_pk)        REFERENCES contact(pk),
    FOREIGN KEY component_pk_fk         (component_pk)      REFERENCES component(pk),
    FOREIGN KEY status_pk_fk            (status_pk)         REFERENCES status(pk),
    FOREIGN KEY imits_status_pk_fk      (imits_status_pk)   REFERENCES imits_status(pk),
    FOREIGN KEY gene_pk_fk              (gene_pk)           REFERENCES gene(pk),
    FOREIGN KEY sent_pk_fk              (sent_pk)           REFERENCES sent(pk)

) COLLATE=utf8_general_ci ENGINE=InnoDb;


-- POPULATE STATIC TABLES

INSERT INTO component (name) VALUES ('disease'), ('gene'), ('phenotype');

INSERT INTO status (status) VALUES
    ('more_phenotyping_data_available'),
    ('mouse_produced'),
    ('mouse_production_started'),
    ('not_planned'),
    ('phenotyping_data_available'),
    ('production_and_phenotyping_planned'),
    ('unregister'),
    ('withdrawn');

INSERT INTO imits_status (status, status_pk) VALUES
    ('Aborted - ES Cell QC Failed', (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ('Assigned - ES Cell QC Complete', (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ('Assigned - ES Cell QC In Progress', (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ('Assigned', (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ('Chimeras obtained', (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ('Chimeras/Founder obtained', (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ('Conflict', (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ('Cre Excision Complete', (SELECT pk FROM status WHERE status = 'mouse_produced')),
    ('Cre Excision Started', (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ('Founder obtained', (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ('Genotype confirmed', (SELECT pk FROM status WHERE status = 'mouse_produced')),
    ('Inactive', (SELECT pk FROM status WHERE status = 'withdrawn')),
    ('Inspect - Conflict', (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ('Inspect - GLT Mouse', (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ('Inspect - MI Attempt', (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ('Interest', (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ('Micro-injection aborted', (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ('Micro-injection in progress', (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ('Mouse Allele Modification Registered', (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ('Phenotype Attempt Registered', NULL),
    ('Phenotype Production Aborted', NULL),
    ('Phenotyping Complete', (SELECT pk FROM status WHERE status = 'phenotyping_data_available')),
    ('Phenotyping Production Registered', NULL),
    ('Phenotyping Started', NULL),
    ('Rederivation Complete', (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ('Rederivation Started', (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ('Withdrawn', (SELECT pk FROM status WHERE status = 'withdrawn'));

SET AUTOCOMMIT = 1;
/*
INSERT INTO status_imits_status(imits_status_pk, status_pk) VALUES
    ((SELECT pk FROM imits_status WHERE status = 'Aborted - ES Cell QC Failed'),            (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ((SELECT pk FROM imits_status WHERE status = 'Assigned - ES Cell QC Complete'),         (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ((SELECT pk FROM imits_status WHERE status = 'Assigned - ES Cell QC In Progress'),      (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ((SELECT pk FROM imits_status WHERE status = 'Assigned'),                               (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ((SELECT pk FROM imits_status WHERE status = 'Chimeras obtained'),                      (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ((SELECT pk FROM imits_status WHERE status = 'Chimeras/Founder obtained'),              (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ((SELECT pk FROM imits_status WHERE status = 'Conflict'),                               (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ((SELECT pk FROM imits_status WHERE status = 'Cre Excision Complete'),                  (SELECT pk FROM status WHERE status = 'mouse_produced')),
    ((SELECT pk FROM imits_status WHERE status = 'Cre Excision Started'),                   (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ((SELECT pk FROM imits_status WHERE status = 'Founder obtained'),                       (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ((SELECT pk FROM imits_status WHERE status = 'Genotype confirmed'),                     (SELECT pk FROM status WHERE status = 'mouse_produced')),
    ((SELECT pk FROM imits_status WHERE status = 'Inactive'),                               (SELECT pk FROM status WHERE status = 'withdrawn')),
    ((SELECT pk FROM imits_status WHERE status = 'Inspect - Conflict'),                     (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ((SELECT pk FROM imits_status WHERE status = 'Inspect - GLT Mouse'),                    (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ((SELECT pk FROM imits_status WHERE status = 'Inspect - MI Attempt'),                   (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ((SELECT pk FROM imits_status WHERE status = 'Interest'),                               (SELECT pk FROM status WHERE status = 'production_and_phenotyping_planned')),
    ((SELECT pk FROM imits_status WHERE status = 'Micro-injection aborted'),                (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ((SELECT pk FROM imits_status WHERE status = 'Micro-injection in progress'),            (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ((SELECT pk FROM imits_status WHERE status = 'Mouse Allele Modification Registered'),   (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ((SELECT pk FROM imits_status WHERE status = 'Phenotype Attempt Registered'),           (SELECT pk FROM status WHERE status = '')),
    ((SELECT pk FROM imits_status WHERE status = 'Phenotype Production Aborted'),           (SELECT pk FROM status WHERE status = '')),
    ((SELECT pk FROM imits_status WHERE status = 'Phenotyping Complete'),                   (SELECT pk FROM status WHERE status = 'phenotyping_data_available')),
    ((SELECT pk FROM imits_status WHERE status = 'Phenotyping Production Registered'),      (SELECT pk FROM status WHERE status = '')),
    ((SELECT pk FROM imits_status WHERE status = 'Phenotyping Started'),                    (SELECT pk FROM status WHERE status = '')),
    ((SELECT pk FROM imits_status WHERE status = 'Rederivation Complete'),                  (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ((SELECT pk FROM imits_status WHERE status = 'Rederivation Started'),                   (SELECT pk FROM status WHERE status = 'mouse_production_started')),
    ((SELECT pk FROM imits_status WHERE status = 'Withdrawn'),                              (SELECT pk FROM status WHERE status = 'withdrawn'));
*/

SET FOREIGN_KEY_CHECKS = 1;