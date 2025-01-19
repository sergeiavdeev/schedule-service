CREATE TABLE price
(
    id           uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    resource_id  uuid NOT NULL,
    rate         numeric(15, 2) NOT NULL,
    price        numeric(15, 2) NOT NULL
);

create index price_resource_id_index
    on schedule.price (resource_id);

insert into price(resource_id, rate, price)
values
    ('def45a47-7458-41ff-8b2c-acdab8821d84', 0, 500),
    ('def45a47-7458-41ff-8b2c-acdab8821d84', 2, 450)
;

CREATE TABLE debt
(
    id           uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    created_at   timestamp WITHOUT TIME ZONE default now(),
    order_id     uuid NOT NULL,
    dt           numeric(15, 2) NOT NULL,
    kt           numeric(15, 2) NOT NULL
);

create index debt_order_id_index
    on schedule.debt (order_id);
