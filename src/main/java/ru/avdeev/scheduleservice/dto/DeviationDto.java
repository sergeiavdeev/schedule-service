package ru.avdeev.scheduleservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class DeviationDto {

    private UUID id;
    private UUID calendarId;
    private LocalDate date;
    private TimeIntervalDto timeInterval;
}
