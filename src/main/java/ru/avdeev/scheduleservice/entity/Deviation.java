package ru.avdeev.scheduleservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Table("deviation")
public record Deviation(
        @Id
        @Column("id")
        UUID id,

        @Column("calendar_id")
        UUID calendarId,

        @Column("deviation_date")
        LocalDate date,

        @Column("start_time")
        LocalTime startTime,

        @Column("end_time")
        LocalTime endTime
) {
}
