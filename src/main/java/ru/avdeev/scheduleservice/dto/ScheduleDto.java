package ru.avdeev.scheduleservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID calendarId;
    private Integer dayOfWeek;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private TimeIntervalDto timeInterval;

    private List<TimeIntervalDto> timeIntervals;
}
