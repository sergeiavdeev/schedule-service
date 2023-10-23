create index calendar_owner_id_index
    on schedule.calendar (owner_id);

create index deviation_calendar_id_index
    on schedule.deviation (calendar_id);

create index schedule_calendar_id_index
    on schedule.schedule (calendar_id);
