SET @now = NOW();
INSERT INTO contact (address, active, created_at) VALUES
  ('user1@ebi.ac.uk', 1, @now),
  ('user2@ebi.ac.uk', 1, @now),
  ('user3@ebi.ac.uk', 1, @now);

INSERT INTO gene (mgi_accession_id, symbol, created_at) VALUES
  ('MGI:0000010', 'gene-10', @now),
  ('MGI:0000020', 'gene-20', @now),
  ('MGI:0000030', 'gene-30', @now);

insert into gene_contact(contact_pk, gene_pk, created_at) values
  (1, 1, @now),
  (1, 2, @now),
  (1, 3, @now),
  (2, 1, @now),
  (3, 1, @now)
;