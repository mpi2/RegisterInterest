SET @now = NOW();
INSERT INTO contact (address, created_at) VALUES
  ('user1@ebi.ac.uk', @now),
  ('user2@ebi.ac.uk', @now),
  ('user3@ebi.ac.uk', @now);

INSERT INTO gene (mgi_accession_id, symbol,    ri_assignment_status,   ri_conditional_allele_production_status, ri_null_allele_production_status, ri_phenotyping_status,  created_at) VALUES
                ('MGI:0000010',    'gene-10', 'riAssignmentStatus-1', 'riConditionalAlleleProductionStatus-1', 'riNullAlleleProductionStatus-1', 'riPhenotypingStatus-1', @now),
                ('MGI:0000020',    'gene-20', 'riAssignmentStatus-2', 'riConditionalAlleleProductionStatus-2', 'riNullAlleleProductionStatus-2', 'riPhenotypingStatus-2', @now),
                ('MGI:0000030',    'gene-30', 'riAssignmentStatus-3', 'riConditionalAlleleProductionStatus-3', 'riNullAlleleProductionStatus-3', 'riPhenotypingStatus-3', @now);

insert into contact_gene(contact_pk, gene_pk, created_at) values
  (1, 1, @now),
  (1, 2, @now),
  (1, 3, @now),
  (2, 1, @now),
  (3, 1, @now)
;