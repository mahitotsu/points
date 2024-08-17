--
create function generate_id() returns uuid as $$
    select (
        lpad(to_char(now(), 'YYYYMMDD'), 8, '0')
        || '-' 
        || lpad(to_char(now(), 'HH24MI'), 4, '0')
        || '-' 
        || lpad(to_char(now(), 'SS'), 4, '0')
        || '-' 
        || lpad(to_char(now(), 'MS'), 4, '0')
        || '-' 
        || lpad(to_hex((random() * (2^(12*4)))::bigint), 12, '0') 
    )::uuid
$$ language sql;
 
--
create table EVENT_STORE (
    TARGET_TYPE varchar not null,
    TARGET_ID uuid not null,
    EVENT_TYPE varchar not null,
    EVENT_ID uuid null,
    EVENT_PAYLOAD jsonb,
    primary key (EVENT_ID)
);