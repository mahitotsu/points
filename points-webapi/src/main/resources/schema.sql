CREATE OR REPLACE FUNCTION generate_id(tm TIMESTAMP(3), tx BIGINT) RETURNS UUID AS $$
    SELECT (
        lpad(to_hex(extract(epoch from tm)::bigint), 12, '0') || '7' || lpad(to_hex((random() * 2^(4*3))::integer), 3, '0') ||
        to_hex(8 | (random() * 3)::integer) || lpad(to_hex((random() * 2^(4*3))::integer), 3, '0') || lpad(to_hex(tx & (2^(4*12) - 1)::bigint), 12, '0')
    )::UUID
$$LANGUAGE SQL IMMUTABLE STRICT;

CREATE TABLE event (
    tm TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT statement_timestamp(),
    tx BIGINT NOT NULL DEFAULT txid_current(),
    id UUID NOT NULL GENERATED ALWAYS AS (generate_id(tm, tx)) STORED,
    PRIMARY KEY (id)
); 