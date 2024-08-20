CREATE OR REPLACE FUNCTION generate_id(tm TIMESTAMP(3), tx BIGINT) RETURNS UUID AS $$
    SELECT (
        lpad(to_hex(extract(epoch from tm)::bigint * 1000 + to_char(tm, 'MS')::integer), 12, '0') || '7' || lpad(to_hex((random() * 2^(4*3))::integer), 3, '0') ||
        to_hex(8 | (random() * 3)::integer) || lpad(to_hex((random() * 2^(4*3))::integer), 3, '0') || lpad(to_hex(tx & (2^(4*12) - 1)::bigint), 12, '0')
    )::UUID
$$LANGUAGE SQL IMMUTABLE STRICT;

CREATE TABLE entity_base (
    tm TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT statement_timestamp(),
    tx BIGINT NOT NULL DEFAULT txid_current(),
    id UUID NOT NULL GENERATED ALWAYS AS (generate_id(tm, tx)) STORED,
    name VARCHAR NOT NULL,
    PRIMARY KEY (id)
); 

CREATE TABLE event (
    id UUID NOT NULL,
    name VARCHAR NOT NULL,
    targetId UUID NOT NULL,
    payload JSONB,
    FOREIGN KEY (id) REFERENCES entity_base(id),
    FOREIGN KEY (targetId) REFERENCES entity_base(id),
    PRIMARY KEY (id)
);

CREATE TABLE account (
    id UUID NOT NULL,
    branch_code CHAR(3) NOT NULL,
    account_number CHAR(7) NOT NULL,
    FOREIGN KEY (id) REFERENCES entity_base(id),
    UNIQUE (branch_code, account_number),
    PRIMARY KEY (id)
);