CREATE OR REPLACE FUNCTION nextval_tempseq ()
RETURNS bigint
LANGUAGE plpgsql
VOLATILE AS '
BEGIN
    RETURN nextval(''tempseq'');
END';

--
CREATE TABLE entity_base (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    ts timestamp NOT NULL DEFAULT statement_timestamp(),
    tx bigint NOT NULL DEFAULT txid_current(),
    sq bigint NOT NULL DEFAULT nextval_tempseq(),
    nm varchar NOT NULL,
    PRIMARY KEY (id)
);

--
CREATE TABLE account_status (
    id uuid NOT NULL,
    branch_code char(3) NOT NULL,
    account_number char(7) NOT NULL,
    status smallint NOT NULL,
    FOREIGN KEY (id) REFERENCES entity_base (id),
    PRIMARY KEY (id)
);