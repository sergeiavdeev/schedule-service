package ru.avdeev.scheduleservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("price")
public record Price(
        @Id
        UUID id,

        @Column("resource_id")
        UUID resourceId,

        @Column("rate")
        Double rate,

        @Column("price")
        Double price
) {
}
