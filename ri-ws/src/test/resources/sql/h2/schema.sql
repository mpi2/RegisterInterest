DROP TABLE IF EXISTS component;
CREATE TABLE component (
  pk          INT         NOT NULL      IDENTITY PRIMARY KEY,
  name        VARCHAR(64) NOT NULL,     UNIQUE(name),
  updated_at  TIMESTAMP   NOT NULL      DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS gene;
CREATE TABLE gene (
  pk                                          INT          NOT NULL  AUTO_INCREMENT PRIMARY KEY,

  mgi_accession_id                            VARCHAR(32)  NOT NULL       UNIQUE,
  symbol                                      VARCHAR(128) NOT NULL,
  assigned_to                                 VARCHAR(128) DEFAULT NULL,
  assignment_status                           VARCHAR(128) DEFAULT NULL,
  assignment_status_date                      DATETIME     DEFAULT NULL,
  assignment_status_pk                        INT          DEFAULT NULL,

  conditional_allele_production_centre        VARCHAR(128) DEFAULT NULL,
  conditional_allele_production_status        VARCHAR(128) DEFAULT NULL,
  conditional_allele_production_status_date   DATETIME     DEFAULT NULL,
  conditional_allele_production_status_pk     INT          DEFAULT NULL,

  null_allele_production_centre               VARCHAR(128) DEFAULT NULL,
  null_allele_production_status               VARCHAR(128) DEFAULT NULL,
  null_allele_production_status_date          DATETIME     DEFAULT NULL,
  null_allele_production_status_pk            INT          DEFAULT NULL,

  phenotyping_centre                          VARCHAR(128) DEFAULT NULL,
  phenotyping_status                          VARCHAR(128) DEFAULT NULL,
  phenotyping_status_date                     DATETIME     DEFAULT NULL,
  phenotyping_status_pk                       INT          DEFAULT NULL,

  number_of_significant_phenotypes            INT          DEFAULT 0,

  updated_at                                  TIMESTAMP    NOT NULL   DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS contact;
CREATE TABLE contact (
  pk              INT          NOT NULL      IDENTITY PRIMARY KEY,
  address         VARCHAR(256) NOT NULL,
  active          INT          NOT NULL      DEFAULT 1,                   -- 1 = active; 0 = inactive
  updated_at      TIMESTAMP    NOT NULL      DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS contact_gene;
CREATE TABLE contact_gene (
  pk          INT       NOT NULL      IDENTITY PRIMARY KEY,
  contact_pk  INT       NOT NULL,
  gene_pk     INT       NOT NULL,
  updated_at  TIMESTAMP NOT NULL      DEFAULT CURRENT_TIMESTAMP,

  UNIQUE (contact_pk, gene_pk)

);


DROP TABLE IF EXISTS imits_status;
CREATE TABLE imits_status (
  pk             INT          NOT NULL      IDENTITY PRIMARY KEY,
  status_pk      INT                        DEFAULT NULL,
  status         VARCHAR(64)  NOT NULL,     UNIQUE(status),
  active         INT          NOT NULL      DEFAULT 1,                   -- 1 = active; 0 = inactive
  updated_at     TIMESTAMP    NOT NULL      DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS status;
CREATE TABLE status (
  pk             INT          NOT NULL      IDENTITY PRIMARY KEY,
  status         VARCHAR(64)  NOT NULL,     UNIQUE(status),
  active         INT          NOT NULL      DEFAULT 1,                   -- 1 = active; 0 = inactive
  updated_at     TIMESTAMP    NOT NULL      DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS sent;
CREATE TABLE sent (
  pk               INT            NOT NULL        AUTO_INCREMENT PRIMARY KEY,
  subject          VARCHAR(78)    NOT NULL,
  body             VARCHAR(2048)  NOT NULL,
  component_pk     INT NOT NULL,
  contact_gene_pk  INT NOT NULL,
  status_pk        INT NOT NULL,
  updated_at       TIMESTAMP      NOT NULL      DEFAULT CURRENT_TIMESTAMP,

  UNIQUE (contact_gene_pk)

);


DROP TABLE IF EXISTS log;
CREATE TABLE log (
  pk                 INT            NOT NULL      IDENTITY PRIMARY KEY,
  contact_pk         INT            NOT NULL,
  component_pk       INT,
  status_pk          INT,
  imits_status_pk    INT,
  gene_pk            INT,
  sent_pk            INT,
  message            VARCHAR(2048)  NOT NULL,
  updated_at         TIMESTAMP      NOT NULL      DEFAULT CURRENT_TIMESTAMP

);


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