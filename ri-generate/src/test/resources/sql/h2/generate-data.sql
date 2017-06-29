SET @now = NOW();
SET @nowplus1 = DATEADD('DAY', 1, @now);
SET @nowplus2 = DATEADD('DAY', 2, @now);
SET @nowplus3 = DATEADD('DAY', 3, @now);

SET @PK_MPDA = (SELECT pk FROM gene_status WHERE status = 'more_phenotyping_data_available');
SET @PK_MP = (SELECT pk FROM gene_status WHERE status = 'mouse_produced');
SET @PK_MPS = (SELECT pk FROM gene_status WHERE status = 'mouse_production_started');
SET @PK_NP = (SELECT pk FROM gene_status WHERE status = 'not_planned');
SET @PK_PDA = (SELECT pk FROM gene_status WHERE status = 'phenotyping_data_available');
SET @PK_PAPP = (SELECT pk FROM gene_status WHERE status = 'production_and_phenotyping_planned');
SET @PK_W = (SELECT pk FROM gene_status WHERE status = 'withdrawn');

INSERT INTO contact (address, active, created_at) VALUES
  ('user1@ebi.ac.uk', 1, @now),
  ('user2@ebi.ac.uk', 1, @now),
  ('user3@ebi.ac.uk', 1, @now);

INSERT INTO gene
  (mgi_accession_id,   symbol,     assigned_to,  assignment_status, assignment_status_date, assignment_status_pk, conditional_allele_production_centre,  conditional_allele_production_status, conditional_allele_production_status_date, conditional_allele_production_status_pk,  null_allele_production_centre,  null_allele_production_status, null_allele_production_status_date, null_allele_production_status_pk,  phenotyping_centre, phenotyping_status, phenotyping_status_date, phenotyping_status_pk, number_of_significant_phenotypes, created_at) VALUES
  ('MGI:0000010',     'gene-010', 'c-01',       'not_planned',      @now,                   @PK_NP,              'caps-01',                             'mouse_produced',                      @nowplus1,                                 @PK_MP,                                  'naps-01',                      'mouse_produced',               @nowplus2,                          @PK_MP,                           'ps-01',             @PK_xx,             @nowplus3,               @PK_PDA,               0,                                @now),
  ('MGI:0000020',     'gene-020', 'c-02',       'withdrawn',        @now,                   @PK_W,               'caps-02',                             'mouse_produced',                      @nowplus1,                                 @PK_MP,                                  'naps-02',                      'mouse_produced',               @nowplus2,                          @PK_MP,                           'ps-02',             @PK_xx,             @nowplus3,               @PK_PDA,               1,                                @now),
  ('MGI:0000030',     'gene-030', 'c-03',       'PAPP',             @now,                   @PK_PAPP,            'caps-03',                              null,                                 @nowplus1,                                 NULL,                                    'naps-03',                       NULL,                          @nowplus2,                          NULL,                             'ps-03',             @PK_xx,             @nowplus3,               @PK_PDA,               2,                                @now),
  ('MGI:0000040',     'gene-040', 'c-04',       'PAPP',             @now,                   @PK_PAPP,            'caps-04',                             'mouse_production_started',            @nowplus1,                                 @PK_MPS,                                 'naps-04',                       NULL,                          @nowplus2,                          NULL,                             'ps-04',             @PK_xx,             @nowplus3,               NULL,                  3,                                @now),
  ('MGI:0000050',     'gene-050', 'c-05',       'PAPP',             @now,                   @PK_PAPP,            'caps-05',                             'mouse_produced',                      @nowplus1,                                 @PK_MP,                                  'naps-05',                       NULL,                          @nowplus2,                          NULL,                             'ps-05',             @PK_xx,             @nowplus3,               NULL,                  4,                                @now),
  ('MGI:0000060',     'gene-060', 'c-06',       'PAPP',             @now,                   @PK_PAPP,            'caps-06',                              null,                                 @nowplus1,                                 NULL,                                    'naps-06',                      'mouse_production_started',     @nowplus2,                          @PK_MPS,                          'ps-06',             @PK_xx,             @nowplus3,               @PK_PDA,               5,                                @now),
  ('MGI:0000070',     'gene-070', 'c-07',       'PAPP',             @now,                   @PK_PAPP,            'caps-07',                              null,                                 @nowplus1,                                 NULL,                                    'naps-07',                      'mouse_produced',               @nowplus2,                          @PK_MP,                           'ps-07',             @PK_xx,             @nowplus3,               @PK_MPDA,              6,                                @now),
  ('MGI:0000080',     'gene-080', 'c-08',       'PAPP',             @now,                   @PK_PAPP,            'caps-08',                             'mouse_produced',                      @nowplus1,                                 @PK_MP,                                  'naps-08',                      'mouse_production_started',     @nowplus2,                          @PK_MPS,                          'ps-08',             @PK_xx,             @nowplus3,               NULL,                  7,                                @now),
  ('MGI:0000090',     'gene-090', 'c-09',       'PAPP',             @now,                   @PK_PAPP,            'caps-09',                             'mouse_production_started',            @nowplus1,                                 @PK_MPS,                                 'naps-09',                      'mouse_produced',               @nowplus2,                          @PK_MP,                           'ps-09',             @PK_xx,             @nowplus3,               NULL,                  8,                                @now),
  ('MGI:0000100',     'gene-100', 'c-10',       'PAPP',             @now,                   @PK_PAPP,            'caps-10',                             'mouse_produced',                      @nowplus1,                                 @PK_MP,                                  'naps-10',                      'mouse_production_started',     @nowplus2,                          @PK_MPS,                          'ps-10',             @PK_xx,             @nowplus3,               @PK_PDA,               9,                                @now),
  ('MGI:0000110',     'gene-110', 'c-11',       'PAPP',             @now,                   @PK_PAPP,            'caps-11',                             'mouse_production_started',            @nowplus1,                                 @PK_MPS,                                 'naps-11',                      'mouse_produced',               @nowplus2,                          @PK_MP,                           'ps-11',             @PK_xx,             @nowplus3,               @PK_PDA,              10,                                @now)
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
  (3, 11, @now)
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
  ('my subject', 'my body',  11,               @PK_PAPP,             @PK_MPS,                                 @PK_MP,                           NULL,                  @now,       @now)
;