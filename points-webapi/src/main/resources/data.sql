WITH inserted_entity AS ( INSERT INTO entity_base (dt) VALUES ('AccountStatusEvent') RETURNING id)
INSERT INTO account_status_event (id, branch_code, account_number, status)
SELECT id, '001', '0000000', 0 FROM inserted_entity;