package ru.avdeev.scheduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class TimeIntervalDto {

    private LocalTime startTime;
    private LocalTime endTime;
}
