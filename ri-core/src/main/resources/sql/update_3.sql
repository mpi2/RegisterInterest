
-- Replace gene_sent.contact_gene_pk (and its requisite foreign key and index) with unconstrained contact address and gene mgi_accession_id.
-- First, add the two columns. Then migrate existing data. Finally, drop the column, foreign key, and index.
ALTER TABLE gene_sent
  ADD COLUMN address VARCHAR(255) NOT NULL AFTER body,
  ADD COLUMN mgi_accession_id VARCHAR(32) NOT NULL AFTER address;


UPDATE gene_sent gs
  JOIN contact_gene cg ON cg.pk = gs.contact_gene_pk
  JOIN gene         g  ON g. pk = cg.gene_pk
  JOIN contact      c  ON c. pk = cg.contact_pk
SET gs.address = c.address, gs.mgi_accession_id = g.mgi_accession_id;


ALTER TABLE gene_sent
  DROP COLUMN contact_gene_pk,
  DROP FOREIGN KEY gene_sent_ibfk_1,
  DROP INDEX gene_contact_pk_fk;


-- Replace gene_sent_summary.contact_pk (and its requisite foreign key and index) with unconstrained contact address.
-- First, add the column. Then migrate existing data. Finally, drop the column, foreign key, and index.
ALTER TABLE gene_sent_summary
  ADD COLUMN address VARCHAR(255) NOT NULL after body;


UPDATE gene_sent_summary gss
  JOIN contact c ON c.pk = gss.contact_pk
SET gss.address = c.address;


ALTER TABLE gene_sent_summary
  DROP COLUMN contact_pk,
  DROP FOREIGN KEY gene_sent_summary_ibfk_1,
  DROP INDEX contact_pk_fk;