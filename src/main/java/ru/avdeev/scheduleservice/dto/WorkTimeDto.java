package ru.avdeev.scheduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkTimeDto {

    private UUID ownerId;
    private UUID calendarId;
    List<DateWorkTime> dateWorkTimeList;
}
