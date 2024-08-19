--
create function generate_id() returns uuid as $$
    with ct as ( select to_char(current_timestamp, 'YYYYMMDDHH24MISSUS') as t)
    select
        (  substring(t from 1 for 8)
        || '-' 
        || substring(t from 9 for 4)
        || '-' 
        || substring(t from 13 for 4)
        || '-' 
        || substring(t from 17 for 4)
        || '-' 
        || lpad(to_hex((random() * (2^(12*4-1)))::bigint), 12, '0')
        )::uuid
    from ct
$$ language sql;
 
--
create table EVENT_STORE (
    EVENT_ID uuid not null default generate_id(),
    EVENT_TYPE varchar not null,
    EVENT_PAYLOAD jsonb,
    TARGET_TYPE varchar not null,
    TARGET_ID uuid not null,
    primary key (EVENT_ID)
);

--
create table ACCOUNT (
    ENTITY_ID uuid not null default generate_id(),
    BRANCH_CODE char(3) not null,
    ACCOUNT_NUMBER char(7) not null,
    constraint UQ_ACCOUNT unique (BRANCH_CODE, ACCOUNT_NUMBER),
    primary key (ENTITY_ID)
);