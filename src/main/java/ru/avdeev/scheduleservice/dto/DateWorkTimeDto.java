package ru.avdeev.scheduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateWorkTimeDto {

    private LocalDate date;
    private Integer dayOfWeek;
    private List<TimeIntervalDto> timeIntervals;
}