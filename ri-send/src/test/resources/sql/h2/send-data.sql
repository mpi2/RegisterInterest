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
  (mgi_accession_id,    symbol,     assigned_to,  assignment_status, assignment_status_date, ri_assignment_status, conditional_allele_production_centre,  conditional_allele_production_status, conditional_allele_production_status_date, conditional_allele_production_start_date, ri_conditional_allele_production_status, null_allele_production_centre,  null_allele_production_status, null_allele_production_status_date, null_allele_production_start_date, ri_null_allele_production_status, phenotyping_centre, phenotyping_status, phenotyping_status_date, ri_phenotyping_status, number_of_significant_phenotypes, created_at) VALUES
  ('MGI:0000010',      'gene-010', 'c-010',       @NP,               @now,                   @NP,                  'caps-010',                            @MP,                                  @nowplus1,                                @nowplus2,                                 @MP,                                     'naps-010',                     @MP,                           @nowplus3,                          @nowplus4,                         @MP,                             'ps-01',             @PK_xx,             @nowplus5,               @PDA,                  0,                                @now)
;

SET @gcUserPk = 1;
insert into contact_gene(contact_pk, gene_pk, created_at) VALUES
  (@gcUserPk,  1, @now)
;

INSERT INTO gene_sent
  (address,            mgi_accession_id,    created_at, sent_at) VALUES
  ('mrelac@ebi.ac.uk', 'MGI:0000010',        @now,       NULL)
;