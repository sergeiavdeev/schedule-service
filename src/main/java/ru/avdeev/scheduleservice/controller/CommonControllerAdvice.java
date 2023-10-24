package ru.avdeev.scheduleservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.avdeev.scheduleservice.dto.ErrorDto;
import ru.avdeev.scheduleservice.exception.ApiException;

@ControllerAdvice
public class CommonControllerAdvice {

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErrorDto> resourceNotFond(ApiException ex) {

        return ResponseEntity
                .status(ex.getHttpStatus().code())
                .body(new ErrorDto(ex.getHttpStatus().code(), ex.getMessage(), ""));
    }
}
