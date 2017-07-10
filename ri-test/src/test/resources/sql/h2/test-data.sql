SET @now = '2017-07-01 11:59:00';
SET @nowplus1 = '2017-07-02 11:59:00';
SET @nowplus2 = '2017-07-03 11:59:00';
SET @nowplus3 = '2017-07-04 11:59:00';

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
  ('pm9@ebi.ac.uk', 1, @now)
;

INSERT INTO gene
  (mgi_accession_id,   symbol,     assigned_to,  assignment_status, assignment_status_date, assignment_status_pk, conditional_allele_production_centre,  conditional_allele_production_status, conditional_allele_production_status_date, conditional_allele_production_status_pk,  null_allele_production_centre,  null_allele_production_status, null_allele_production_status_date, null_allele_production_status_pk,  phenotyping_centre, phenotyping_status, phenotyping_status_date, phenotyping_status_pk, number_of_significant_phenotypes, created_at) VALUES
  ('MGI:0000010',     'gene-010',  'c-010',      @NP,               @now,                   @PK_NP,               'caps-010',                            @MP,                                  @nowplus1,                                 @PK_MP,                                   'naps-010',                     'mouse_produced',              @nowplus2,                          @PK_MP,                            'ps-010',           @PK_xx,             @nowplus3,               @PK_PDA,               0,                                @now),
  ('MGI:0000020',     'gene-020',  'c-020',      @W,                @now,                   @PK_W,                'caps-020',                            @MP,                                  @nowplus1,                                 @PK_MP,                                   'naps-020',                     'mouse_produced',              @nowplus2,                          @PK_MP,                            'ps-020',           @PK_xx,             @nowplus3,               @PK_PDA,               1,                                @now),
  ('MGI:0000030',     'gene-030',  'c-030',      @PAPP,             @now,                   @PK_PAPP,             'caps-030',                            null,                                 @nowplus1,                                 NULL,                                     'naps-030',                      NULL,                         @nowplus2,                          NULL,                              'ps-030',           @PK_xx,             @nowplus3,               @PK_PDA,               2,                                @now),
  ('MGI:0000040',     'gene-040',  'c-040',      @PAPP,             @now,                   @PK_PAPP,             'caps-040',                            @MPS,                                 @nowplus1,                                 @PK_MPS,                                  'naps-040',                      NULL,                         @nowplus2,                          NULL,                              'ps-040',           @PK_xx,             @nowplus3,               NULL,                  3,                                @now),
  ('MGI:0000050',     'gene-050',  'c-050',      @PAPP,             @now,                   @PK_PAPP,             'caps-050',                            @MP,                                  @nowplus1,                                 @PK_MP,                                   'naps-050',                      NULL,                         @nowplus2,                          NULL,                              'ps-050',           @PK_xx,             @nowplus3,               NULL,                  4,                                @now),
  ('MGI:0000060',     'gene-060',  'c-060',      @PAPP,             @now,                   @PK_PAPP,             'caps-060',                            null,                                 @nowplus1,                                 NULL,                                     'naps-060',                     'mouse_production_started',    @nowplus2,                          @PK_MPS,                           'ps-060',           @PK_xx,             @nowplus3,               @PK_PDA,               5,                                @now),
  ('MGI:0000070',     'gene-070',  'c-070',      @PAPP,             @now,                   @PK_PAPP,             'caps-070',                            null,                                 @nowplus1,                                 NULL,                                     'naps-070',                     'mouse_produced',              @nowplus2,                          @PK_MP,                            'ps-070',           @PK_xx,             @nowplus3,               @PK_MPDA,              6,                                @now),
  ('MGI:0000080',     'gene-080',  'c-080',      @PAPP,             @now,                   @PK_PAPP,             'caps-080',                            @MP,                                  @nowplus1,                                 @PK_MP,                                   'naps-080',                     'mouse_production_started',    @nowplus2,                          @PK_MPS,                           'ps-080',           @PK_xx,             @nowplus3,               NULL,                  7,                                @now),
  ('MGI:0000090',     'gene-090',  'c-090',      @PAPP,             @now,                   @PK_PAPP,             'caps-090',                            @MPS,                                 @nowplus1,                                 @PK_MPS,                                  'naps-090',                     'mouse_produced',              @nowplus2,                          @PK_MP,                            'ps-090',           @PK_xx,             @nowplus3,               NULL,                  8,                                @now),
  ('MGI:0000100',     'gene-100',  'c-100',      @PAPP,             @now,                   @PK_PAPP,             'caps-100',                            @MP,                                  @nowplus1,                                 @PK_MP,                                   'naps-100',                     'mouse_production_started',    @nowplus2,                          @PK_MPS,                           'ps-100',           @PK_xx,             @nowplus3,               @PK_PDA,               9,                                @now),
  ('MGI:0000110',     'gene-110',  'c-110',      @PAPP,             @now,                   @PK_PAPP,             'caps-110',                            @MPS,                                 @nowplus1,                                 @PK_MPS,                                  'naps-110',                     'mouse_produced',              @nowplus2,                          @PK_MP,                            'ps-110',           @PK_xx,             @nowplus3,               @PK_PDA,              10,                                @now),
  ('MGI:0000120',     'gene-120',  'c-120',      @PAPP,             @now,                   @PK_PAPP,             'caps-120',                            @MPS,                                 @nowplus1,                                 @PK_MPS,                                  'naps-120',                     'mouse_produced',              @nowplus2,                          @PK_MP,                            'ps-120',           @PK_xx,             @nowplus3,               @PK_PDA,              11,                                @now)
;

SET @gcUserPk = 1;
insert into gene_contact(contact_pk, gene_pk, created_at) VALUES
  (@gcUserPk,  1, @now),
  (@gcUserPk,  2, @now),
  (@gcUserPk,  3, @now),
  (@gcUserPk,  4, @now),
  (@gcUserPk,  5, @now),
  (@gcUserPk,  6, @now),
  (@gcUserPk,  7, @now),
  (@gcUserPk,  8, @now),
  (@gcUserPk,  9, @now),
  (@gcUserPk, 10, @now),
  (@gcUserPk, 11, @now),
  (@gcUserPk, 12, @now)
;

INSERT INTO gene_sent
  (subject,       body,      gene_contact_pk, assignment_status_pk, conditional_allele_production_status_pk, null_allele_production_status_pk, phenotyping_status_pk, created_at, sent_at) VALUES
  ('my subject', 'my body',   4,               @PK_NP,               NULL,                                    NULL,                             NULL,                  @now,       NULL),
  ('my subject', 'my body',   5,               @PK_PAPP,             @PK_MPS,                                 NULL,                             NULL,                  @now,       NULL),
  ('my subject', 'my body',   6,               @PK_PAPP,             NULL,                                    @PK_MP,                           @PK_PDA,               @now,       NULL),
  ('my subject', 'my body',   7,               @PK_PAPP,             NULL,                                    @PK_MPS,                          @PK_MPDA,              @now,       NULL),
  ('my subject', 'my body',   8,               @PK_PAPP,             @PK_MP,                                  @PK_MP,                           NULL,                  @now,       NULL),
  ('my subject', 'my body',   9,               @PK_PAPP,             @PK_MP,                                  @PK_MP,                           NULL,                  @now,       NULL),
  ('my subject', 'my body',  10,               @PK_PAPP,             @PK_MP,                                  @PK_MPS,                          NULL,                  @now,       NULL),
  ('my subject', 'my body',  11,               @PK_PAPP,             @PK_MPS,                                 @PK_MP,                           NULL,                  @now,       NULL),
  ('my subject', 'my body',  12,               @PK_PAPP,             @PK_MPS,                                 @PK_MP,                           NULL,                  @now,       NULL)
;