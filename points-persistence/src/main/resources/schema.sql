--
create function generate_id() returns uuid as $$
    select (
        lpad(to_char(current_timestamp, 'YYYYMMDD'), 8, '0')
        || '-' 
        || lpad(to_char(current_timestamp, 'HH24MI'), 4, '0')
        || '-' 
        || lpad(to_char(current_timestamp, 'SS'), 4, '0')
        || '-' 
        || lpad(to_char(current_timestamp, 'MS'), 4, '0')
        || '-' 
        || lpad(to_hex((random() * (2^(8*4-1)))::integer), 8, '0')
        || lpad(to_hex((random() * (2^(4*4-1)))::integer), 4, '0')
    )::uuid
$$ language sql;
 
--
create table EVENT_STORE (
    TARGET_TYPE varchar not null,
    TARGET_ID uuid not null,
    EVENT_TYPE varchar not null,
    EVENT_ID uuid null default generate_id(),
    EVENT_PAYLOAD jsonb,
    primary key (EVENT_ID)
);