CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table transactions
(
    id        uuid PRIMARY KEY,
    client_id uuid,
    amount    numeric(15, 2),
    currency  varchar(255),
    market_id uuid,
    mcc       integer,
    time      timestamptz
);

create table bonuses
(
    id           uuid PRIMARY KEY DEFAULT uuid_generate_v1(),
    client_id    uuid,
    amount       numeric(15, 2),
    create_time  timestamptz,
    update_time  timestamptz,
    strategy_id  uuid,
    time_to_live timestamptz
);

create table bonuses_transactions
(
    bonus_uuid       uuid REFERENCES bonuses (id) ON DELETE CASCADE,
    transaction_uuid uuid REFERENCES transactions (id) ON DELETE CASCADE,
    CONSTRAINT bonuses_transactions_pkey PRIMARY KEY (bonus_uuid, transaction_uuid)
);

create table tariff_plans
(
    id    uuid PRIMARY KEY DEFAULT uuid_generate_v1(),
    title text
);

create table clients
(
    id             uuid PRIMARY KEY,
    first_name     text,
    last_name      text,
    tariff_plan_id uuid REFERENCES tariff_plans (id) ON DELETE NO ACTION
);

create table strategies
(
    id             uuid PRIMARY KEY,
    title          text NOT NULL,
    type           text NOT NULL,
    settings       text NOT NULL,
    tariff_plan_id uuid REFERENCES tariff_plans (id) ON DELETE NO ACTION
);

create table aggregated_strategy_processing
(
    id          uuid PRIMARY KEY DEFAULT uuid_generate_v1(),
    strategy_id uuid REFERENCES strategies (id) ON DELETE CASCADE,
    next_time   timestamptz
);
