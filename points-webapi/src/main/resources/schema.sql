CREATE OR REPLACE FUNCTION generate_uuid ()
RETURNS uuid
LANGUAGE plpgsql
VOLATILE AS '
DECLARE
    ts  timestamp;
    tx  bigint;
    sq  bigint;

    mil bigint;
    mic bigint;

    msb char(16);
    lsb char(16);

BEGIN
    ts := statement_timestamp();
    tx := (txid_current() &  x''FFFFFFFF''::bigint) | x''80000000''::bigint;
    sq := nextval(''tempseq'') &  x''FFFFFFFF''::bigint;

    mil := (extract(epoch from ts) * 1000)::bigint;
    mic := (extract(microseconds from ts) % 1000)::bigint;

    msb := lpad(to_hex(mil), 12, ''0'') || ''7'' || lpad(to_hex(mic), 3, ''0'');
    lsb := lpad(to_hex(tx), 8, ''0'') || lpad(to_hex(sq), 8, ''0'');

    RETURN (msb || lsb)::uuid;
END;';

--
CREATE TABLE account_status (
    id uuid NOT NULL DEFAULT generate_uuid(),
    branch_code char(3) NOT NULL,
    account_number char(7) NOT NULL,
    status smallint NOT NULL CHECK (status BETWEEN 0 AND 1),
    PRIMARY KEY (id)
);