
create table transactions (
    id          uuid    PRIMARY KEY,
    client_id   uuid,
    amount      numeric(15,2),
    currency    varchar(255),
    market_id   uuid,
    mcc         integer,
    time        timestamptz
);

