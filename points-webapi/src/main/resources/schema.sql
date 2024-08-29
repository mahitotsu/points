CREATE OR REPLACE FUNCTION nextval_tempseq ()
RETURNS bigint
LANGUAGE plpgsql
VOLATILE AS '
BEGIN
    RETURN nextval(''tempseq'');
END;';
