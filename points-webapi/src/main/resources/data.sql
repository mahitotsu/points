WITH entity_base AS (INSERT INTO entities (name) VALUES ('Account') RETURNING id)
INSERT INTO accounts ( id, branch_code, account_number, status)
SELECT id, '001', '0000000', 0 FROM entity_base;

WITH entity_base AS (INSERT INTO entities (name) VALUES ('Account') RETURNING id)
INSERT INTO accounts ( id, branch_code, account_number, status)
SELECT id, '002', '0000000', 0 FROM entity_base;

WITH entity_base AS (INSERT INTO entities (name) VALUES ('Account') RETURNING id)
INSERT INTO accounts ( id, branch_code, account_number, status)
SELECT id, '003', '0000000', 0 FROM entity_base;