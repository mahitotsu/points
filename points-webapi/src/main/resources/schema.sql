CREATE OR REPLACE FUNCTION init_session()
RETURNS void
LANGUAGE plpgsql
VOLATILE
AS '
BEGIN
    CREATE TEMP SEQUENCE IF NOT EXISTS tempseq AS integer CYCLE;
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
    unix_ms bigint;
    microseconds integer;
    result uuid;
BEGIN
    unix_ms := (EXTRACT(EPOCH FROM ts) * 1000)::bigint;
    microseconds := (EXTRACT(MICROSECONDS FROM ts)::integer % 1000);
    result := (
        lpad(to_hex(unix_ms), 12, ''0'') || ''7'' || lpad(to_hex(microseconds), 3, ''0'')  ||
        ''8'' || lpad(to_hex(tx), 8, ''0'') || lpad(to_hex(sq), 7, ''0'')
    )::uuid;
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