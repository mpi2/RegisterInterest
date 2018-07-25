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

INSERT INTO contact (pk, address, created_at) VALUES
  (1, 'mrelac@ebi.ac.uk', @now),
  (2, 'jmason@ebi.ac.uk', @now)
;

INSERT INTO gene
  (mgi_accession_id,   symbol,     assigned_to,  assignment_status, assignment_status_date, ri_assignment_status, conditional_allele_production_centre,  conditional_allele_production_status, ri_conditional_allele_production_status, conditional_allele_production_status_date,  conditional_allele_production_start_date, null_allele_production_centre,  null_allele_production_status, ri_null_allele_production_status, null_allele_production_status_date, null_allele_production_start_date, phenotyping_centre, phenotyping_status, phenotyping_status_date, ri_phenotyping_status, number_of_significant_phenotypes, created_at) VALUES
  ('MGI:1919199',     'Cers5',     'c-010',      @PAPP,             @now,                   @PAPP,                'caps-010',                            @MP,                                  @MP,                                     @nowplus1,                                  @nowplus2,                                'naps-010',                     @MP,                           @MP,                              @nowplus3,                          @nowplus4,                         'ps-010',           @PDA,               @nowplus5,               @PDA,                  0,                                @now),
  ('MGI:103576',      'Ccl11',     'c-020',      @W,                @now,                   @W,                   'caps-020',                            NULL,                                 NULL,                                    @nowplus1,                                  @nowplus2,                                'naps-020',                     NULL,                          NULL,                             @nowplus3,                          @nowplus4,                         'ps-020',           @PK_xx,             @nowplus5,               NULL,                  0,                                @now),
  ('MGI:2444824',     'Sirpb1a',   'c-030',      NULL,              @now,                   NULL,                 'caps-030',                            NULL,                                 NULL,                                    @nowplus1,                                  @nowplus2,                                'naps-030',                     @MP,                           @MP,                              @nowplus3,                          @nowplus4,                         'ps-030',           @PK_xx,             @nowplus5,               NULL,                  0,                                @now),
  ('MGI:2443658',     'Prr14l',    'c-040',      @PAPP,             @now,                   @PAPP,                'caps-040',                            NULL,                                 NULL,                                    @nowplus1,                                  @nowplus2,                                'naps-040',                     @MPS,                          @MPS,                             @nowplus3,                          @nowplus4,                         'ps-040',           @PK_xx,             @nowplus5,               NULL,                  0,                                @now),
  ('MGI:3576659',     'Ano5',      'c-120',      @PAPP,             @now,                   @PAPP,                'caps-120',                            @MPS,                                 @MPS,                                    @nowplus1,                                  @nowplus2,                                'naps-120',                     @MPS,                          @MPS,                             @nowplus3,                          @nowplus4,                         'ps-120',           @PK_xx,             @nowplus5,               NULL,                  0,                                @now)
;

insert into contact_gene(contact_pk, gene_pk, created_at) VALUES
  (1,  1, @now),
  (1,  2, @now),
  (1,  3, @now),
  (1,  4, @now),
  (1,  5, @now),
  (2,  1, @now),
  (2,  2, @now),
  (2,  3, @now),
  (2,  4, @now),
  (2,  5, @now)
;