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
  ('mrelac@ebi.ac.uk', @now)
;

INSERT INTO gene
  (mgi_accession_id,   symbol,     assigned_to,  assignment_status, assignment_status_date, assignment_status_pk, ri_assignment_status, conditional_allele_production_centre,  conditional_allele_production_status, conditional_allele_production_status_date, conditional_allele_production_start_date, conditional_allele_production_status_pk,  ri_conditional_allele_production_status, null_allele_production_centre,  null_allele_production_status, null_allele_production_status_date, null_allele_production_start_date, null_allele_production_status_pk,  ri_null_allele_production_status, phenotyping_centre, phenotyping_status, phenotyping_status_date, phenotyping_status_pk, ri_phenotyping_status, number_of_significant_phenotypes, created_at) VALUES
  ('MGI:0000010',      'gene-010', 'c-010',      @NP,               @now,                   @PK_NP,               @NP,                  'caps-010',                            @MP,                                  @nowplus1,                                @nowplus2,                                 @PK_MP,                                   @MP,                                     'naps-010',                     @MP,                           @nowplus3,                          @nowplus4,                          @PK_MP,                           @MP,                              'ps-01',             @PK_xx,             @nowplus5,               @PK_PDA,              @PDA,                  0,                                @now)
;

SET @gcUserPk = 1;
insert into contact_gene(contact_pk, gene_pk, created_at) VALUES
  (@gcUserPk,  1, @now)
;

INSERT INTO gene_sent
  (subject,       body,      contact_gene_pk, assignment_status_pk, conditional_allele_production_status_pk, null_allele_production_status_pk, phenotyping_status_pk, created_at, sent_at) VALUES
  ('my subject', 'my body',  1,               @PK_NP,               NULL,                                    NULL,                             NULL,                  @now,       NULL)
;