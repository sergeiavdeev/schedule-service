package ru.avdeev.scheduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDto {
    private Integer httpStatus;
    private String message;
    private String stackTrace;
}
