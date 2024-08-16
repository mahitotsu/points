CREATE SEQUENCE account_number_seq INCREMENT 1 START 1;

CREATE TABLE account (
    account_number char(10) PRIMARY KEY,
    account_status integer NOT NULL
);