CREATE TABLE calendar (

    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    owner_id uuid NOT NULL,
    user_id uuid NOT NULL,
    start_date date NOT NULL,
    name varchar(50)
);

CREATE TABLE schedule (

    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    calendar_id uuid NOT NULL,
    day_of_week int2 NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL
);

CREATE TABLE deviation (

    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    calendar_id uuid NOT NULL,
    deviation_date date NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL
);

INSERT INTO calendar (id, owner_id, user_id, start_date, name)
VALUES (
    '505a2194-7603-4d24-8f5e-b222e764b10f',
    'd3541a1d-b22e-4203-aca0-330e7b1248ca',
    '0fbddea2-195f-4a06-b139-cf92c2455126',
    '2023-05-01',
    'Пн-пт 9 - 19, Сб-вс 9 - 17, обед с 12 до 13'
);

INSERT INTO schedule (calendar_id, day_of_week, start_time, end_time)
VALUES
    ('505a2194-7603-4d24-8f5e-b222e764b10f', 1, '09:00', '12:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', 1, '13:00', '19:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', 2, '09:00', '12:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', 2, '13:00', '19:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', 3, '09:00', '12:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', 3, '13:00', '19:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', 4, '09:00', '12:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', 4, '13:00', '19:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', 5, '09:00', '12:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', 5, '13:00', '19:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', 6, '09:00', '12:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', 6, '13:00', '17:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', 7, '09:00', '12:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', 7, '13:00', '17:00')
;

INSERT INTO deviation (calendar_id, deviation_date, start_time, end_time)
VALUES
    ('505a2194-7603-4d24-8f5e-b222e764b10f', '2023-09-14', '10:00', '12:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', '2023-09-14', '13:00', '16:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', '2023-09-17', '08:00', '12:00'),
    ('505a2194-7603-4d24-8f5e-b222e764b10f', '2023-09-17', '13:00', '22:00')
;



