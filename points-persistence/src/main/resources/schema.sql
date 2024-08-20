--
CREATE TABLE event (
    event_time TIMESTAMP NOT NULL default statement_timestamp(),
    event_txid BIGINT NOT NULL default txid_current(),
    event_wxyz DOUBLE PRECISION NOT NULL default random(),
    event_name VARCHAR NOT NULL,
    event_payload JSONB,
    target_id UUID NOT NULL,
    target_name VARCHAR NOT NULL,
    PRIMARY KEY (event_time, event_txid, event_wxyz)
);

--
CREATE TABLE account (
    entity_id UUID NOT NULL DEFAULT gen_random_uuid(),
    branch_code CHAR(3) NOT NULL,
    account_number CHAR(7) NOT NULL,
    CONSTRAINT uq_branch_account UNIQUE (branch_code, account_number),
    PRIMARY KEY (entity_id)
);