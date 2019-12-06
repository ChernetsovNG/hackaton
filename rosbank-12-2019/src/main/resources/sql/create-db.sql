CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table transactions (
    id          uuid    PRIMARY KEY,
    client_id   uuid,
    amount      numeric(15,2),
    currency    varchar(255),
    market_id   uuid,
    mcc         integer,
    time        timestamptz
);

create table bonuses (
    id              uuid    PRIMARY KEY  DEFAULT uuid_generate_v1(),
    client_id       uuid,
    amount          numeric(15,2),
    create_time     timestamptz,
    update_time     timestamptz,
    strategy_id     uuid
);

create table bonuses_transactions (
     bonus_uuid         uuid  REFERENCES bonuses (id) ON DELETE CASCADE,
     transaction_uuid   uuid  REFERENCES transactions (id) ON DELETE CASCADE,
     CONSTRAINT bonuses_transactions_pkey PRIMARY KEY (bonus_uuid, transaction_uuid)
);
