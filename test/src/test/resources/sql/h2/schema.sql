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
);


DROP TABLE IF EXISTS contact;
CREATE TABLE contact (

  pk                INT          NOT NULL         AUTO_INCREMENT PRIMARY KEY,
  address           VARCHAR(255) NOT NULL UNIQUE,
  password          VARCHAR(256) NOT NULL         DEFAULT '',
  password_expired  INT          NOT NULL         DEFAULT 1,                          -- 1 = expired; 0 = not expired
  account_locked    INT          NOT NULL         DEFAULT 0,                          -- 1 = locked; 0 = not locked

  created_at        DATETIME     NOT NULL,
  updated_at        TIMESTAMP    NOT NULL         DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS contact_role;
CREATE TABLE contact_role (
  pk             INT          NOT NULL      AUTO_INCREMENT PRIMARY KEY,

  contact_pk     INT          NOT NULL,
  role           VARCHAR(64)  NOT NULL      DEFAULT 'USER',

  created_at     DATETIME     NOT NULL,
  updated_at     TIMESTAMP    NOT NULL      DEFAULT CURRENT_TIMESTAMP,

  UNIQUE (contact_pk, role)

);


DROP TABLE IF EXISTS reset_credentials;
CREATE TABLE reset_credentials (
  email_address  VARCHAR(255) NOT NULL PRIMARY KEY,
  token          TEXT         NOT NULL,
  created_at     DATETIME     NOT NULL

);


DROP TABLE IF EXISTS contact_gene;
CREATE TABLE contact_gene (
  pk             INT          NOT NULL      AUTO_INCREMENT PRIMARY KEY,

  contact_pk     INT          NOT NULL,
  gene_pk        INT          NOT NULL,

  created_at     DATETIME     NOT NULL,
  updated_at     TIMESTAMP    NOT NULL        DEFAULT CURRENT_TIMESTAMP,

  UNIQUE (contact_pk, gene_pk)

);


DROP TABLE IF EXISTS imits_status;
CREATE TABLE imits_status (
  pk              INT          NOT NULL           AUTO_INCREMENT PRIMARY KEY,

  gene_status_pk  INT                             DEFAULT NULL,
  status          VARCHAR(64)  NOT NULL UNIQUE,

  created_at      DATETIME     NOT NULL,
  updated_at      TIMESTAMP    NOT NULL           DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS gene_status;
CREATE TABLE gene_status (
  pk          INT          NOT NULL           AUTO_INCREMENT PRIMARY KEY,

  status      VARCHAR(64)  NOT NULL UNIQUE,

  created_at  DATETIME     NOT NULL,
  updated_at  TIMESTAMP    NOT NULL           DEFAULT CURRENT_TIMESTAMP

);


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

);


DROP TABLE IF EXISTS gene_sent_summary;
CREATE TABLE gene_sent_summary (
  pk                                          INT             NOT NULL        AUTO_INCREMENT PRIMARY KEY,
  subject                                     VARCHAR(78)     NOT NULL,
  body                                        TEXT            NOT NULL,
  contact_pk                                  INT             NOT NULL,

  created_at                                  DATETIME        NOT NULL,
  sent_at                                     DATETIME,                       -- a null value means 'generated but not geneSentSummary yet'.
  updated_at                                  TIMESTAMP       NOT NULL        DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS log;


-- POPULATE STATIC TABLES

SET @now=NOW();

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