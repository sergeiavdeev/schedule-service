package ru.avdeev.scheduleservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalTime;
import java.util.UUID;

@Table("schedule")
public record Schedule(
        @Id
        UUID id,

        @Column("calendar_id")
        UUID calendarId,

        @Column("day_of_week")
        Short dayOfWeek,

        @Column("start_time")
        LocalTime startTime,

        @Column("end_time")
        LocalTime endTime
) {
}
