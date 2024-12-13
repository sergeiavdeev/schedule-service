CREATE TABLE booking
(
    id           uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    resource_id  uuid NOT NULL,
    user_id      uuid NOT NULL,
    booking_date date NOT NULL,
    start_time   TIME NOT NULL,
    end_time     TIME NOT NULL
);

create index booking_resource_id_index
    on schedule.booking (resource_id);

create index booking_user_id_index
    on schedule.booking (user_id);

create index booking_date_index
    on schedule.booking (booking_date);