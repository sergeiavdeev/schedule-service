package ru.avdeev.scheduleservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DeviationDto {

    private UUID calendarId;
    private LocalDate date;
    private List<TimeIntervalDto> timeIntervals;
}
