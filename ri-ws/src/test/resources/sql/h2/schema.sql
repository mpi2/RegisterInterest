DROP TABLE IF EXISTS component;
CREATE TABLE component (
  pk          INT         NOT NULL      IDENTITY PRIMARY KEY,
  name        VARCHAR(64) NOT NULL,     UNIQUE(name),
  updated_at  TIMESTAMP   NOT NULL      DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS gene;
CREATE TABLE gene (
  pk                INT         NOT NULL      IDENTITY PRIMARY KEY,
  mgi_accession_id  VARCHAR(32) NOT NULL,     UNIQUE(mgi_accession_id),
  updated_at        TIMESTAMP   NOT NULL      DEFAULT CURRENT_TIMESTAMP

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


DROP TABLE IF EXISTS status_imits_status;
CREATE TABLE status_imits_status (
  pk                  INT         NOT NULL        IDENTITY PRIMARY KEY,
  status_pk           INT                         DEFAULT NULL,
  imits_status_pk     INT         NOT NULL,
  updated_at          TIMESTAMP      NOT NULL     DEFAULT CURRENT_TIMESTAMP,

  UNIQUE (status_pk, imits_status_pk),

);


DROP TABLE IF EXISTS sent;
CREATE TABLE sent (
  pk               INT            NOT NULL      IDENTITY PRIMARY KEY,
  subject          VARCHAR(78)    NOT NULL,
  body             VARCHAR(2048)  NOT NULL,
  contact_pk       INT NOT NULL,
  component_pk     INT NOT NULL,
  status_pk        INT NOT NULL,
  gene_pk          INT,
  updated_at       TIMESTAMP      NOT NULL      DEFAULT CURRENT_TIMESTAMP

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