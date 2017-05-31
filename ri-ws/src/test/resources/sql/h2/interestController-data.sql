INSERT INTO contact (address, active) VALUES
  ('mrelac@ebi.ac.uk', 1),
  ('tmeehan@ebi.ac.uk', 1),
  ('jmason@ebi.ac.uk', 1);

INSERT INTO gene (mgi_accession_id) VALUES
  ('MGI:0000010'),
  ('MGI:0000020'),
  ('MGI:0000030');

insert into contact_gene(contact_pk, gene_pk) values
  (1, 1),
  (1, 2),
  (1, 3),
  (2, 1),
  (3, 1)
;