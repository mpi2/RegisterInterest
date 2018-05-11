SET @now = '2017-07-01 11:59:00';
SET @nowplus1 = '2017-07-02 11:59:00';
SET @nowplus2 = '2017-07-03 11:59:00';
SET @nowplus3 = '2017-07-04 11:59:00';
SET @nowplus4 = '2017-07-05 11:59:00';
SET @nowplus5 = '2017-07-06 11:59:00';

SET @MPDA = 'more_phenotyping_data_available';
SET @MP   = 'mouse_produced';
SET @MPS  = 'mouse_production_started';
SET @NP   = 'not_planned';
SET @PDA  = 'phenotyping_data_available';
SET @PAPP = 'production_and_phenotyping_planned';
SET @W    = 'withdrawn';

SET @PK_MPDA = (SELECT pk FROM gene_status WHERE status = @MPDA);
SET @PK_MP = (SELECT pk FROM gene_status WHERE status = @MP);
SET @PK_MPS = (SELECT pk FROM gene_status WHERE status = @MPS);
SET @PK_NP = (SELECT pk FROM gene_status WHERE status = @NP);
SET @PK_PDA = (SELECT pk FROM gene_status WHERE status = @PDA);
SET @PK_PAPP = (SELECT pk FROM gene_status WHERE status = @PAPP);
SET @PK_W = (SELECT pk FROM gene_status WHERE status = @W);

INSERT INTO contact (pk, address, active, created_at) VALUES
  (1, 'mrelac@ebi.ac.uk', 1, @now),
  (2, 'jmason@ebi.ac.uk', 1, @now)
;

INSERT INTO gene
  (mgi_accession_id,   symbol,     assigned_to,  assignment_status, assignment_status_date, assignment_status_pk, conditional_allele_production_centre,  conditional_allele_production_status, conditional_allele_production_status_pk,  conditional_allele_production_status_date,  conditional_allele_production_start_date, null_allele_production_centre,  null_allele_production_status, null_allele_production_status_pk,  null_allele_production_status_date, null_allele_production_start_date, phenotyping_centre, phenotyping_status, phenotyping_status_date, phenotyping_status_pk, number_of_significant_phenotypes, created_at) VALUES
  ('MGI:1919199',     'Cers5',     'c-010',      @PAPP,             @now,                   @PK_PAPP,             'caps-010',                            @MP,                                  @PK_MP,                                   @nowplus1,                                  @nowplus2,                                'naps-010',                     @MP,                           @PK_MP,                            @nowplus3,                          @nowplus4,                         'ps-010',           @PDA,               @nowplus5,               @PK_PDA,               0,                                @now),
  ('MGI:103576',      'Ccl11',     'c-020',      @W,                @now,                   @PK_W,                'caps-020',                            NULL,                                 NULL,                                     @nowplus1,                                  @nowplus2,                                'naps-020',                     NULL,                          NULL,                              @nowplus3,                          @nowplus4,                         'ps-020',           @PK_xx,             @nowplus5,               NULL,                  0,                                @now),
  ('MGI:2444824',     'Sirpb1a',   'c-030',      NULL,              @now,                   NULL,                 'caps-030',                            NULL,                                 NULL,                                     @nowplus1,                                  @nowplus2,                                'naps-030',                     @MP,                           @PK_MP,                            @nowplus3,                          @nowplus4,                         'ps-030',           @PK_xx,             @nowplus5,               NULL,                  0,                                @now),
  ('MGI:2443658',     'Prr14l',    'c-040',      @PAPP,             @now,                   @PK_PAPP,             'caps-040',                            NULL,                                 NULL,                                     @nowplus1,                                  @nowplus2,                                'naps-040',                     @MPS,                          @PK_MPS,                           @nowplus3,                          @nowplus4,                         'ps-040',           @PK_xx,             @nowplus5,               NULL,                  0,                                @now),
  ('MGI:3576659',     'Ano5',      'c-120',      @PAPP,             @now,                   @PK_PAPP,             'caps-120',                            @MPS,                                 @PK_MPS,                                  @nowplus1,                                  @nowplus2,                                'naps-120',                     @MPS,                          @PK_MPS,                           @nowplus3,                          @nowplus4,                         'ps-120',           @PK_xx,             @nowplus5,               NULL,                  0,                                @now)
;

insert into gene_contact(contact_pk, gene_pk, created_at) VALUES
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