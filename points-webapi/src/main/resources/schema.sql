CREATE OR REPLACE FUNCTION next_seq_val()
RETURNS bigint
LANGUAGE plpgsql 
VOLATILE
AS '
BEGIN
    RETURN nextval(''tempseq'');
END;
';

CREATE OR REPLACE FUNCTION gen_uuid_for_entity(ts timestamp, tx bigint , sq bigint)
RETURNS uuid
LANGUAGE plpgsql
IMMUTABLE
RETURNS NULL ON NULL INPUT
AS '
DECLARE
    unix_t char(12);
    rand_a char(3);
    rand_b char(16);
    result uuid;
BEGIN
    unix_t := lpad(to_hex((EXTRACT(EPOCH FROM ts) * 1000)::bigint), 12, ''0'');
    rand_a := lpad(to_hex(EXTRACT(MICROSECONDS FROM ts)::integer % 1000), 3, ''0'');
    rand_b := to_hex((''x'' || right(md5(to_hex(tx) || to_hex(sq)), 16))::bit(64)::bigint & ~(3::bigint << 62) | (2::bigint << 62));
    result := ( unix_t || ''7'' || rand_a || rand_b)::uuid;
    RETURN result;
END;
';

--
CREATE TABLE entities (
    ts timestamp NOT NULL DEFAULT statement_timestamp(),
    tx bigint NOT NULL DEFAULT txid_current(),
    sq bigint NOT NULL DEFAULT  next_seq_val(),
    id uuid NOT NULL GENERATED ALWAYS AS (gen_uuid_for_entity(ts, tx, sq)) STORED,
    name varchar NOT NULL,
    UNIQUE (ts, tx, sq),
    PRIMARY KEY (id)
);

--
CREATE TABLE accounts (
    id uuid NOT NULL,
    branch_code char(3) NOT NULL,
    account_number char(7) NOT NULL,
    status smallint NOT NULL,
    UNIQUE (branch_code, account_number),
    FOREIGN KEY (id) REFERENCES entities (id),
    PRIMARY KEY (id)
);