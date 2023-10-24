package ru.avdeev.scheduleservice.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final HttpResponseStatus httpStatus;

    public ApiException(HttpResponseStatus httpStatus, String message, Object... args) {
        super(String.format(message, args));
        this.httpStatus = httpStatus;
    }
}
