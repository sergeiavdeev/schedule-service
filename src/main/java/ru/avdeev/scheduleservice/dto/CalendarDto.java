package ru.avdeev.scheduleservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CalendarDto {

    private UUID id;
    private UUID ownerId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID userId;
    private LocalDate startDate;
    private String name;
}
