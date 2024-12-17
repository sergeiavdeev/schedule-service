ALTER TABLE booking
ADD COLUMN created_at timestamp WITHOUT TIME ZONE default now()
;