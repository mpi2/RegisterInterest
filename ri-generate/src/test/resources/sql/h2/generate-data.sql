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
  ('user1@ebi.ac.uk', 1, @now),
  ('user2@ebi.ac.uk', 1, @now),
  ('user3@ebi.ac.uk', 1, @now),
  ('user4@ebi.ac.uk', 1, @now)
;

INSERT INTO gene
  (mgi_accession_id,   symbol,     assigned_to,  assignment_status, assignment_status_date, assignment_status_pk, conditional_allele_production_centre,  conditional_allele_production_status, conditional_allele_production_status_pk,  conditional_allele_production_status_date,  conditional_allele_production_start_date, null_allele_production_centre,  null_allele_production_status, null_allele_production_status_pk,  null_allele_production_status_date, null_allele_production_start_date, phenotyping_centre, phenotyping_status, phenotyping_status_date, phenotyping_status_pk, number_of_significant_phenotypes, created_at) VALUES
  ('MGI:0000010',     'gene-010',  'c-010',      @NP,               @now,                   @PK_NP,               'caps-010',                            @MP,                                  @PK_MP,                                   @nowplus1,                                  @nowplus2,                                'naps-010',                     @MP,                           @PK_MP,                            @nowplus3,                          @nowplus4,                            'ps-010',           @PK_xx,             @nowplus5,               @PK_PDA,               0,                                @now),
  ('MGI:0000020',     'gene-020',  'c-020',      @W,                @now,                   @PK_W,                'caps-020',                            @MP,                                  @PK_MP,                                   @nowplus1,                                  @nowplus2,                                'naps-020',                     @MP,                           @PK_MP,                            @nowplus3,                          @nowplus4,                            'ps-020',           @PK_xx,             @nowplus5,               @PK_PDA,               1,                                @now),
  ('MGI:0000030',     'gene-030',  'c-030',      @PAPP,             @now,                   @PK_PAPP,             'caps-030',                            NULL,                                 NULL,                                     @nowplus1,                                  @nowplus2,                                'naps-030',                     NULL,                          NULL,                              @nowplus3,                          @nowplus4,                            'ps-030',           @PK_xx,             @nowplus5,               @PK_PDA,               2,                                @now),
  ('MGI:0000040',     'gene-040',  'c-040',      @PAPP,             @now,                   @PK_PAPP,             'caps-040',                            @MPS,                                 @PK_MPS,                                  @nowplus1,                                  @nowplus2,                                'naps-040',                     NULL,                          NULL,                              @nowplus3,                          @nowplus4,                            'ps-040',           @PK_xx,             @nowplus5,               NULL,                  3,                                @now),
  ('MGI:0000050',     'gene-050',  'c-050',      @PAPP,             @now,                   @PK_PAPP,             'caps-050',                            @MP,                                  @PK_MP,                                   @nowplus1,                                  @nowplus2,                                'naps-050',                     NULL,                          NULL,                              @nowplus3,                          @nowplus4,                            'ps-050',           @PK_xx,             @nowplus5,               NULL,                  4,                                @now),
  ('MGI:0000060',     'gene-060',  'c-060',      @PAPP,             @now,                   @PK_PAPP,             'caps-060',                            NULL,                                 NULL,                                     @nowplus1,                                  @nowplus2,                                'naps-060',                     @MPS,                          @PK_MPS,                           @nowplus3,                          @nowplus4,                            'ps-060',           @PK_xx,             @nowplus5,               @PK_PDA,               5,                                @now),
  ('MGI:0000070',     'gene-070',  'c-070',      @PAPP,             @now,                   @PK_PAPP,             'caps-070',                            NULL,                                 NULL,                                     @nowplus1,                                  @nowplus2,                                'naps-070',                     @MP,                           @PK_MP,                            @nowplus3,                          @nowplus4,                            'ps-070',           @PK_xx,             @nowplus5,               @PK_MPDA,              6,                                @now),
  ('MGI:0000080',     'gene-080',  'c-080',      @PAPP,             @now,                   @PK_PAPP,             'caps-080',                            @MP,                                  @PK_MP,                                   @nowplus1,                                  @nowplus2,                                'naps-080',                     @MPS,                          @PK_MPS,                           @nowplus3,                          @nowplus4,                            'ps-080',           @PK_xx,             @nowplus5,               NULL,                  7,                                @now),
  ('MGI:0000090',     'gene-090',  'c-090',      @PAPP,             @now,                   @PK_PAPP,             'caps-090',                            @MPS,                                 @PK_MPS,                                  @nowplus1,                                  @nowplus2,                                'naps-090',                     @MP,                           @PK_MP,                            @nowplus3,                          @nowplus4,                            'ps-090',           @PK_xx,             @nowplus5,               NULL,                  8,                                @now),
  ('MGI:0000100',     'gene-100',  'c-100',      @PAPP,             @now,                   @PK_PAPP,             'caps-100',                            @MP,                                  @PK_MP,                                   @nowplus1,                                  @nowplus2,                                'naps-100',                     @MPS,                          @PK_MPS,                           @nowplus3,                          @nowplus4,                            'ps-100',           @PK_xx,             @nowplus5,               @PK_PDA,               9,                                @now),
  ('MGI:0000110',     'gene-110',  'c-110',      @PAPP,             @now,                   @PK_PAPP,             'caps-110',                            @MPS,                                 @PK_MPS,                                  @nowplus1,                                  @nowplus2,                                'naps-110',                     @MP,                           @PK_MP,                            @nowplus3,                          @nowplus4,                            'ps-110',           @PK_xx,             @nowplus5,               @PK_PDA,              10,                                @now),
  ('MGI:0000120',     'gene-120',  'c-120',      @PAPP,             @now,                   @PK_PAPP,             'caps-120',                            @MPS,                                 @PK_MPS,                                  @nowplus1,                                  @nowplus2,                                'naps-120',                     @MP,                           @PK_MP,                            @nowplus3,                          @nowplus4,                            'ps-120',           @PK_xx,             @nowplus5,               @PK_PDA,              11,                                @now)
;

insert into gene_contact(contact_pk, gene_pk, created_at) VALUES
  (1,  1, @now),
  (1,  2, @now),
  (1,  3, @now),
  (1,  4, @now),
  (2,  5, @now),
  (2,  6, @now),
  (2,  7, @now),
  (2,  8, @now),
  (3,  9, @now),
  (3, 10, @now),
  (3, 11, @now),
  (4, 12, @now)
;

INSERT INTO gene_sent
  (subject,       body,      gene_contact_pk, assignment_status_pk, conditional_allele_production_status_pk, null_allele_production_status_pk, phenotyping_status_pk, created_at, sent_at) VALUES
  ('my subject', 'my body',   4,               @PK_NP,               NULL,                                    NULL,                             NULL,                  @now,       @now),
  ('my subject', 'my body',   5,               @PK_PAPP,             @PK_MPS,                                 NULL,                             NULL,                  @now,       @now),
  ('my subject', 'my body',   6,               @PK_PAPP,             NULL,                                    @PK_MP,                           @PK_PDA,               @now,       @now),
  ('my subject', 'my body',   7,               @PK_PAPP,             NULL,                                    @PK_MPS,                          @PK_MPDA,              @now,       @now),
  ('my subject', 'my body',   8,               @PK_PAPP,             @PK_MP,                                  @PK_MP,                           NULL,                  @now,       @now),
  ('my subject', 'my body',   9,               @PK_PAPP,             @PK_MP,                                  @PK_MP,                           NULL,                  @now,       @now),
  ('my subject', 'my body',  10,               @PK_PAPP,             @PK_MP,                                  @PK_MPS,                          NULL,                  @now,       @now),
  ('my subject', 'my body',  11,               @PK_PAPP,             @PK_MPS,                                 @PK_MP,                           NULL,                  @now,       @now),
  ('my subject', 'my body',  12,               @PK_PAPP,             @PK_MPS,                                 @PK_MP,                           NULL,                  @now,       @now)
;