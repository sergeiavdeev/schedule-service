package ru.avdeev.scheduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeIntervalDto {

    private LocalTime startTime;
    private LocalTime endTime;
}
