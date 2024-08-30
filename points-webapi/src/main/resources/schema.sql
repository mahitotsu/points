--
CREATE SEQUENCE entity_sequence INCREMENT BY 50;
CREATE TABLE entity_base (
    id bigint DEFAULT nextval('entity_sequence') NOT NULL,
    ts timestamp DEFAULT statement_timestamp() NOT NULL,
    tx bigint DEFAULT txid_current() NOT NULL,
    dt varchar NOT NULL,
    PRIMARY KEY (id)
);

--
CREATE TABLE account_status_event (
    id bigint NOT NULL,
    branch_code char(3) NOT NULL,
    account_number char(7) NOT NULL,
    status smallint CHECK (status BETWEEN 0 AND 1) NOT NULL,
    FOREIGN KEY (id) REFERENCES entity_base (id),
    PRIMARY KEY (id)
);

CREATE TABLE point_changed_event (
    id bigint NOT NULL,
    branch_code char(3) NOT NULL,
    account_number char(7) NOT NULL,
    amount integer NOT NULL,
    FOREIGN KEY (id) REFERENCES entity_base (id),
    PRIMARY KEY (id)
);