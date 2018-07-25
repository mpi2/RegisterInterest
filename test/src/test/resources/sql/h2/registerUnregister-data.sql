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
  (mgi_accession_id,   symbol,     assigned_to,  assignment_status, assignment_status_date, ri_assignment_status,  conditional_allele_production_centre, conditional_allele_production_status, ri_conditional_allele_production_status, conditional_allele_production_status_date, conditional_allele_production_start_date,  null_allele_production_centre,  null_allele_production_status, ri_null_allele_production_status,  null_allele_production_status_date, null_allele_production_start_date, phenotyping_centre, phenotyping_status, ri_phenotyping_status, phenotyping_status_date, number_of_significant_phenotypes, created_at) VALUES
  ('MGI:1919199',     'Cers5',    'c-010',       @NP,               @now,                   @NP,                  'caps-010',                            NULL,                                 NULL,                                    @nowplus2,                                 @nowplus1,                                 'naps-010',                     NULL,                          NULL,                              @nowplus4,                          @nowplus3,                        'ps-010',            NULL,               NULL,                  @nowplus5,               NULL,                             @now),
  ('MGI:102851',      'Pdx1',     'c-010',       @NP,               @now,                   @NP,                  'caps-010',                            NULL,                                 NULL,                                    @nowplus2,                                 @nowplus1,                                 'naps-010',                     NULL,                          NULL,                              @nowplus4,                          @nowplus3,                        'ps-010',            NULL,               NULL,                  @nowplus5,               NULL,                             @now)
;