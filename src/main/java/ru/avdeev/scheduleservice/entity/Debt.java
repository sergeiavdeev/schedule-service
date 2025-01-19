package ru.avdeev.scheduleservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("debt")
public record Debt(
    @Id()
    @Column("id")
    UUID id,

    @Column("created_at")
    LocalDateTime createdAt,

    @Column("order_id")
    UUID orderId,

    @Column("dt")
    Double dt,

    @Column("kt")
    Double kt
){}
