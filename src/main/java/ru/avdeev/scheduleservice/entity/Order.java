package ru.avdeev.scheduleservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Table("booking")
public record Order(
        @Id
        UUID id,

        @Column("resource_id")
        UUID resourceId,

        @Column("user_id")
        UUID userId,

        @Column("booking_date")
        LocalDate bookingDate,

        @Column("start_time")
        LocalTime startTime,

        @Column("end_time")
        LocalTime endTime
) {}
