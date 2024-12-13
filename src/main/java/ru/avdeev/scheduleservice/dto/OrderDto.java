package ru.avdeev.scheduleservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class OrderDto {
    private UUID id;
    private UUID storageId;
    private UUID resourceId;
    private UUID userId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
