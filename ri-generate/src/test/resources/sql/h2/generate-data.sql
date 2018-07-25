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
  ('user1@ebi.ac.uk', @now),
  ('user2@ebi.ac.uk', @now),
  ('user3@ebi.ac.uk', @now),
  ('user4@ebi.ac.uk', @now)
;

INSERT INTO gene
  (mgi_accession_id,   symbol,     assigned_to,  assignment_status, assignment_status_date, ri_assignment_status, conditional_allele_production_centre,  conditional_allele_production_status, ri_conditional_allele_production_status, conditional_allele_production_status_date,  conditional_allele_production_start_date, null_allele_production_centre,  null_allele_production_status, ri_null_allele_production_status, null_allele_production_status_date, null_allele_production_start_date, phenotyping_centre, phenotyping_status, phenotyping_status_date, ri_phenotyping_status, number_of_significant_phenotypes, created_at) VALUES
  ('MGI:0000010',     'gene-010',  'c-010',      @NP,               @now,                   @NP,                  'caps-010',                            @MP,                                  @MP,                                     @nowplus1,                                  @nowplus2,                                'naps-010',                     @MP,                           @MP,                              @nowplus3,                          @nowplus4,                         'ps-010',           @PDA,               @nowplus5,               @PDA,                   0,                                @now),
  ('MGI:0000020',     'gene-020',  'c-020',      @W,                @now,                   @W,                   'caps-020',                            @MP,                                  @MP,                                     @nowplus1,                                  @nowplus2,                                'naps-020',                     @MP,                           @MP,                              @nowplus3,                          @nowplus4,                         'ps-020',           @PDA,               @nowplus5,               @PDA,                   1,                                @now),
  ('MGI:0000030',     'gene-030',  'c-030',      @PAPP,             @now,                   @PAPP,                'caps-030',                            NULL,                                 NULL,                                    @nowplus1,                                  @nowplus2,                                'naps-030',                     NULL,                          NULL,                             @nowplus3,                          @nowplus4,                         'ps-030',           @PDA,               @nowplus5,               @PDA,                   2,                                @now),
  ('MGI:0000040',     'gene-040',  'c-040',      @PAPP,             @now,                   @PAPP,                'caps-040',                            @MPS,                                 @MPS,                                    @nowplus1,                                  @nowplus2,                                'naps-040',                     NULL,                          NULL,                             @nowplus3,                          @nowplus4,                         'ps-040',           NULL,               @nowplus5,               NULL,                   3,                                @now),
  ('MGI:0000050',     'gene-050',  'c-050',      @PAPP,             @now,                   @PAPP,                'caps-050',                            @MP,                                  @MP,                                     @nowplus1,                                  @nowplus2,                                'naps-050',                     NULL,                          NULL,                             @nowplus3,                          @nowplus4,                         'ps-050',           NULL,               @nowplus5,               NULL,                   4,                                @now),
  ('MGI:0000060',     'gene-060',  'c-060',      @PAPP,             @now,                   @PAPP,                'caps-060',                            NULL,                                 NULL,                                    @nowplus1,                                  @nowplus2,                                'naps-060',                     @MPS,                          @MPS,                             @nowplus3,                          @nowplus4,                         'ps-060',           @PDA,               @nowplus5,               @PDA,                   5,                                @now),
  ('MGI:0000070',     'gene-070',  'c-070',      @PAPP,             @now,                   @PAPP,                'caps-070',                            NULL,                                 NULL,                                    @nowplus1,                                  @nowplus2,                                'naps-070',                     @MP,                           @MP,                              @nowplus3,                          @nowplus4,                         'ps-070',           @MPDA,              @nowplus5,               @MPDA,                  6,                                @now),
  ('MGI:0000080',     'gene-080',  'c-080',      @PAPP,             @now,                   @PAPP,                'caps-080',                            @MP,                                  @MP,                                     @nowplus1,                                  @nowplus2,                                'naps-080',                     @MPS,                          @MPS,                             @nowplus3,                          @nowplus4,                         'ps-080',           NULL,               @nowplus5,               NULL,                   7,                                @now),
  ('MGI:0000090',     'gene-090',  'c-090',      @PAPP,             @now,                   @PAPP,                'caps-090',                            @MPS,                                 @MPS,                                    @nowplus1,                                  @nowplus2,                                'naps-090',                     @MP,                           @MP,                              @nowplus3,                          @nowplus4,                         'ps-090',           NULL,               @nowplus5,               NULL,                   8,                                @now),
  ('MGI:0000100',     'gene-100',  'c-100',      @PAPP,             @now,                   @PAPP,                'caps-100',                            @MP,                                  @MP,                                     @nowplus1,                                  @nowplus2,                                'naps-100',                     @MPS,                          @MPS,                             @nowplus3,                          @nowplus4,                         'ps-100',           @PDA,               @nowplus5,               @PDA,                   9,                                @now),
  ('MGI:0000110',     'gene-110',  'c-110',      @PAPP,             @now,                   @PAPP,                'caps-110',                            @MPS,                                 @MPS,                                    @nowplus1,                                  @nowplus2,                                'naps-110',                     @MP,                           @MP,                              @nowplus3,                          @nowplus4,                         'ps-110',           @PDA,               @nowplus5,               @PDA,                  10,                                @now),
  ('MGI:0000120',     'gene-120',  'c-120',      @PAPP,             @now,                   @PAPP,                'caps-120',                            @MPS,                                 @MPS,                                    @nowplus1,                                  @nowplus2,                                'naps-120',                     @MP,                           @MP,                              @nowplus3,                          @nowplus4,                         'ps-120',           @PDA,               @nowplus5,               @PDA,                  11,                                @now)
;

insert into contact_gene(contact_pk, gene_pk, created_at) VALUES
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
  (subject,       body,       address,           mgi_accession_id, assignment_status,    conditional_allele_production_status, null_allele_production_status, phenotyping_status, created_at, sent_at) VALUES
  ('my subject', 'my body',  'user1@ebi.ac.uk', 'MGI:0000010',     @NP,                  NULL,                                 NULL,                          NULL,               @now,       @now),
  ('my subject', 'my body',  'user2@ebi.ac.uk', 'MGI:0000020',     @PAPP,                @MPS,                                 NULL,                          NULL,               @now,       @now),
  ('my subject', 'my body',  'user2@ebi.ac.uk', 'MGI:0000030',     @PAPP,                NULL,                                 @_MP,                          @PDA,               @now,       @now),
  ('my subject', 'my body',  'user2@ebi.ac.uk', 'MGI:0000040',     @PAPP,                NULL,                                 @_MPS,                         @MPDA,              @now,       @now),
  ('my subject', 'my body',  'user2@ebi.ac.uk', 'MGI:0000050',     @PAPP,                @MP,                                  @_MP,                          NULL,               @now,       @now),
  ('my subject', 'my body',  'user3@ebi.ac.uk', 'MGI:0000060',     @PAPP,                @MP,                                  @_MP,                          NULL,               @now,       @now),
  ('my subject', 'my body',  'user3@ebi.ac.uk', 'MGI:0000070',     @PAPP,                @MP,                                  @_MPS,                         NULL,               @now,       @now),
  ('my subject', 'my body',  'user3@ebi.ac.uk', 'MGI:0000080',     @PAPP,                @MPS,                                 @_MP,                          NULL,               @now,       @now),
  ('my subject', 'my body',  'user4@ebi.ac.uk', 'MGI:0000090',     @PAPP,                @MPS,                                 @_MP,                          NULL,               @now,       @now)
;