INSERT INTO contact (address, active) VALUES
  ('user1@ebi.ac.uk', 1),
  ('user2@ebi.ac.uk', 1),
  ('user3@ebi.ac.uk', 1);

INSERT INTO gene (mgi_accession_id, symbol) VALUES
  ('MGI:0000010', 'gene-10'),
  ('MGI:0000020', 'gene-20'),
  ('MGI:0000030', 'gene-30');

insert into contact_gene(contact_pk, gene_pk) values
  (1, 1),
  (1, 2),
  (1, 3),
  (2, 1),
  (3, 1)
;