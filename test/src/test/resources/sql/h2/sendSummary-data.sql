SET @now = '2017-07-01 11:59:00';
SET @nowplus1 = '2017-07-02 11:59:00';
SET @nowplus2 = '2017-07-03 11:59:00';
SET @nowplus3 = '2017-07-04 11:59:00';
SET @nowplus4 = '2017-07-05 11:59:00';
SET @nowplus5 = '2017-07-06 11:59:00';

SET @MPDA = 'More phenotyping data available';
SET @MP   = 'Genotype confirmed mice';
SET @MPS  = 'Started';
SET @NP   = 'Not planned';
SET @PDA  = 'Phenotyping data available';
SET @PAPP = 'Selected for production and phenotyping';
SET @W    = 'Withdrawn';

SET @PK_MPDA = (SELECT pk FROM gene_status WHERE status = @MPDA);
SET @PK_MP = (SELECT pk FROM gene_status WHERE status = @MP);
SET @PK_MPS = (SELECT pk FROM gene_status WHERE status = @MPS);
SET @PK_NP = (SELECT pk FROM gene_status WHERE status = @NP);
SET @PK_PDA = (SELECT pk FROM gene_status WHERE status = @PDA);
SET @PK_PAPP = (SELECT pk FROM gene_status WHERE status = @PAPP);
SET @PK_W = (SELECT pk FROM gene_status WHERE status = @W);

INSERT INTO contact (address, created_at) VALUES
  ('mrelac@ebi.ac.uk', @now),
  ('vmunoz@ebi.ac.uk', @now),
  ('jmason@ebi.ac.uk', @now),
  ('pm9@ebi.ac.uk',    @now)
;


INSERT INTO gene
  (mgi_accession_id,   symbol,     assigned_to,  assignment_status, assignment_status_date, ri_assignment_status,  conditional_allele_production_centre,  conditional_allele_production_status, ri_conditional_allele_production_status, conditional_allele_production_status_date, conditional_allele_production_start_date,  null_allele_production_centre,  null_allele_production_status, ri_null_allele_production_status, null_allele_production_status_date, null_allele_production_start_date, phenotyping_centre, phenotyping_status, phenotyping_status_date, ri_phenotyping_status, number_of_significant_phenotypes, created_at) VALUES
  ('MGI:103576',      'Ccl11',    'c-010',       @W,                @now,                   @W,                   'caps-010',                             NULL,                                 NULL,                                    @nowplus2,                                 @nowplus1,                                 'naps-010',                     NULL,                          NULL,                             @nowplus4,                          @nowplus3,                         'ps-010',           NULL,               @nowplus5,               NULL,                  NULL,                             @now),
  ('MGI:1919199',     'Cers5',    'c-010',       @PAPP,             @now,                   @PAPP,                'caps-010',                             @MP,                                  @MP,                                     @nowplus2,                                 @nowplus1,                                 'naps-010',                     @MP,                           @MP,                              @nowplus4,                          @nowplus3,                         'ps-010',           @PDA,               @nowplus5,               @PDA,                  NULL,                             @now),
  ('MGI:2443658',     'Prr14l',   'c-010',       @PAPP,             @now,                   @PAPP,                'caps-010',                             NULL,                                 NULL,                                    @nowplus2,                                 @nowplus1,                                 'naps-010',                     @MP,                           @MP,                              @nowplus4,                          @nowplus3,                         'ps-010',           NULL,               @nowplus5,               NULL,                  NULL,                             @now),
  ('MGI:2444824',     'Sirpb1a',  'c-010',       NULL,              @now,                   NULL,                 'caps-010',                             NULL,                                 NULL,                                    @nowplus2,                                 @nowplus1,                                 'naps-010',                     @MP,                           @MP,                              @nowplus4,                          @nowplus3,                         'ps-010',           NULL,               @nowplus5,               NULL,                  NULL,                             @now),
  ('MGI:3576659',     'Ano5',     'c-010',       @PAPP,             @now,                   @PAPP,                'caps-010',                             @MPS,                                 NULL,                                    @nowplus2,                                 @nowplus1,                                 'naps-010',                     @MPS,                          @MPS,                             @nowplus4,                          @nowplus3,                         'ps-010',           NULL,               @nowplus5,               NULL,                  NULL,                             @now)
;

INSERT INTO gene_sent
  (pk, address,           mgi_accession_id, created_at, sent_at) VALUES
  (1,  'user1@ebi.ac.uk', 'MGI:103576',      NOW(),      NOW()),
  (2,  'user1@ebi.ac.uk', 'MGI:1919199',     NOW(),      NOW()),
  (3,  'user1@ebi.ac.uk', 'MGI:2443658',     NOW(),      NOW()),
  (4,  'user1@ebi.ac.uk', 'MGI:2444824',     NOW(),      NOW()),
  (5,  'user1@ebi.ac.uk', 'MGI:3576659',     NOW(),      NOW())
;