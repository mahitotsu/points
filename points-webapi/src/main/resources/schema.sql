CREATE OR REPLACE FUNCTION init_session()
RETURNS void
LANGUAGE plpgsql
VOLATILE
AS '
BEGIN
    CREATE TEMP SEQUENCE IF NOT EXISTS tempseq AS integer CYCLE;
    ALTER SEQUENCE tempseq RESTART WITH 1;
END;
';

CREATE OR REPLACE FUNCTION next_seq_val()
RETURNS bigint
LANGUAGE plpgsql 
VOLATILE
AS '
BEGIN
    RETURN nextval(''tempseq'');
END;
';

CREATE OR REPLACE FUNCTION gen_uuid_for_entity(ts timestamp, tx integer, sq integer)
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
    tx integer NOT NULL DEFAULT txid_current()::integer,
    sq integer NOT NULL DEFAULT  next_seq_val(),
    id uuid NOT NULL GENERATED ALWAYS AS (gen_uuid_for_entity(ts, tx, sq)) STORED,
    PRIMARY KEY (id)
);