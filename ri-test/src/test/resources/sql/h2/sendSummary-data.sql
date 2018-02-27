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

INSERT INTO contact (address, active, created_at) VALUES
  ('mrelac@ebi.ac.uk', 1, @now),
  ('vmunoz@ebi.ac.uk', 1, @now),
  ('jmason@ebi.ac.uk', 1, @now),
  ('pm9@ebi.ac.uk',    1, @now)
;


INSERT INTO gene
  (mgi_accession_id,   symbol,     assigned_to,  assignment_status, assignment_status_date, assignment_status_pk, conditional_allele_production_centre,  conditional_allele_production_status, conditional_allele_production_status_pk,  conditional_allele_production_status_date, conditional_allele_production_start_date,  null_allele_production_centre,  null_allele_production_status, null_allele_production_status_pk,  null_allele_production_status_date, null_allele_production_start_date, phenotyping_centre, phenotyping_status, phenotyping_status_date, phenotyping_status_pk, number_of_significant_phenotypes, created_at) VALUES
  ('MGI:103576',       'Ccl11',    'c-010',      @W,                @now,                   @PK_W,                'caps-010',                            NULL,                                 NULL,                                     @nowplus2,                                 @nowplus1,                                 'naps-010',                     NULL,                          NULL,                              @nowplus4,                          @nowplus3,                         'ps-010',           NULL,               @nowplus5,               NULL,                  NULL,                             @now),
  ('MGI:1919199',      'Cers5',    'c-010',      @PAPP,             @now,                   @PK_PAPP,             'caps-010',                            @MP,                                  @PK_MP,                                   @nowplus2,                                 @nowplus1,                                 'naps-010',                     @MP,                           @PK_MP,                            @nowplus4,                          @nowplus3,                         'ps-010',           @PDA,               @nowplus5,               @PK_PDA,               NULL,                             @now),
  ('MGI:2443658',      'Prr14l',   'c-010',      @PAPP,             @now,                   @PK_PAPP,             'caps-010',                            NULL,                                 NULL,                                     @nowplus2,                                 @nowplus1,                                 'naps-010',                     @MP,                           @PK_MP,                            @nowplus4,                          @nowplus3,                         'ps-010',           NULL,               @nowplus5,               NULL,                  NULL,                             @now),
  ('MGI:2444824',      'Sirpb1a',  'c-010',      NULL,              @now,                   NULL,                 'caps-010',                            NULL,                                 NULL,                                     @nowplus2,                                 @nowplus1,                                 'naps-010',                     @MP,                           @PK_MP,                            @nowplus4,                          @nowplus3,                         'ps-010',           NULL,               @nowplus5,               NULL,                  NULL,                             @now),
  ('MGI:3576659',      'Ano5',     'c-010',      @PAPP,             @now,                   @PK_PAPP,             'caps-010',                            @MPS,                                 @PK_MPS,                                  @nowplus2,                                 @nowplus1,                                 'naps-010',                     @MPS,                          @PK_MPS,                           @nowplus4,                          @nowplus3,                         'ps-010',           NULL,               @nowplus5,               NULL,                  NULL,                             @now)
;