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
    pk                INT         NOT NULL         AUTO_INCREMENT PRIMARY KEY,
    mgi_accession_id  VARCHAR(32) NOT NULL UNIQUE,
    updated_at        TIMESTAMP   NOT NULL        DEFAULT CURRENT_TIMESTAMP
                        ON UPDATE CURRENT_TIMESTAMP

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


DROP TABLE IF EXISTS imits_status;
CREATE TABLE imits_status (
    pk          INT          NOT NULL           AUTO_INCREMENT PRIMARY KEY,
    status      VARCHAR(64)  NOT NULL UNIQUE,
    active      INT          NOT NULL           DEFAULT 1,                   -- 1 = active; 0 = inactive
    updated_at  TIMESTAMP   NOT NULL        DEFAULT CURRENT_TIMESTAMP
                  ON UPDATE CURRENT_TIMESTAMP

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS status;
CREATE TABLE status (
    pk          INT          NOT NULL           AUTO_INCREMENT PRIMARY KEY,
    status      VARCHAR(64)  NOT NULL UNIQUE,
    active      INT          NOT NULL           DEFAULT 1,                   -- 1 = active; 0 = inactive
    updated_at  TIMESTAMP   NOT NULL        DEFAULT CURRENT_TIMESTAMP
                  ON UPDATE CURRENT_TIMESTAMP

) COLLATE=utf8_general_ci ENGINE=InnoDb;


DROP TABLE IF EXISTS status_imits_status;
CREATE TABLE status_imits_status (
    pk                INT           NOT NULL    AUTO_INCREMENT PRIMARY KEY,
    status_pk         INT                       DEFAULT NULL,
    imits_status_pk   INT           NOT NULL,
    updated_at        TIMESTAMP   NOT NULL        DEFAULT CURRENT_TIMESTAMP
                        ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY status_pk_fk           (status_pk)          REFERENCES status(pk),
    FOREIGN KEY imits_status_pk_fk     (imits_status_pk)    REFERENCES imits_status(pk),
    UNIQUE KEY  status_imits_status_uk (status_pk, imits_status_pk)

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

INSERT INTO imits_status (status) VALUES
    ('Aborted - ES Cell QC Failed'),
    ('Assigned - ES Cell QC Complete'),
    ('Assigned - ES Cell QC In Progress'),
    ('Assigned'),
    ('Chimeras obtained'),
    ('Chimeras/Founder obtained'),
    ('Conflict'),
    ('Cre Excision Complete'),
    ('Cre Excision Started'),
    ('Founder obtained'),
    ('Genotype confirmed'),
    ('Inactive'),
    ('Inspect - Conflict'),
    ('Inspect - GLT Mouse'),
    ('Inspect - MI Attempt'),
    ('Interest'),
    ('Micro-injection aborted'),
    ('Micro-injection in progress'),
    ('Mouse Allele Modification Registered'),
    ('Phenotype Attempt Registered'),
    ('Phenotype Production Aborted'),
    ('Phenotyping Complete'),
    ('Phenotyping Production Registered'),
    ('Phenotyping Started'),
    ('Rederivation Complete'),
    ('Rederivation Started'),
    ('Withdrawn');

SET AUTOCOMMIT = 1;

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


SET FOREIGN_KEY_CHECKS = 1;