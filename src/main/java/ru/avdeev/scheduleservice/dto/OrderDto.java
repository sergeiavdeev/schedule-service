package ru.avdeev.scheduleservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDto {
    private UUID id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID storageId;
    private UUID resourceId;
    private UUID userId;
    private LocalDate bookingDate;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserDto user;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double amount;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double debt;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<DebtDto> debts;

    @JsonProperty(value = "count",access = JsonProperty.Access.READ_ONLY)
    public Double getCount() {
        return (double)Duration.between(startTime, endTime).toMinutes() / 60;
    }
}
