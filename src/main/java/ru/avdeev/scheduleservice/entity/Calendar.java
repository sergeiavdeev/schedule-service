package ru.avdeev.scheduleservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table("calendar")
public record Calendar(
    @Id
    @Column("id")
    UUID id,

    @Column("owner_id")
    UUID ownerId,

    @Column("user_id")
    UUID userId,

    @Column("start_date")
    LocalDate startDate,

    @Column("name")
    String name
) {
}
