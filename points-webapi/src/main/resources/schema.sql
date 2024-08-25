CREATE OR REPLACE FUNCTION next_seq_val() RETURNS bigint AS ' 
BEGIN
    RETURN nextval(''tempseq'');
END;
' LANGUAGE plpgsql;

CREATE TABLE entities (
    tx bigint NOT NULL DEFAULT txid_current(),
    sq bigint NOT NULL DEFAULT  next_seq_val(),
    ts timestamp NOT NULL DEFAULT statement_timestamp(),
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    PRIMARY KEY (id)
);