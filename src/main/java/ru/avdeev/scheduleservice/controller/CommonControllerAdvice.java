package ru.avdeev.scheduleservice.controller;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.avdeev.scheduleservice.dto.ErrorDto;
import ru.avdeev.scheduleservice.exception.ApiException;

import java.util.Arrays;

@ControllerAdvice
@Slf4j
public class CommonControllerAdvice {

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErrorDto> resourceNotFond(ApiException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                .status(ex.getHttpStatus().code())
                .body(new ErrorDto(ex.getHttpStatus().code(), ex.getMessage(), ""));
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ErrorDto> anyException(RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                .body(new ErrorDto(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), ex.getMessage(), Arrays.toString(ex.getStackTrace())));
    }
}
