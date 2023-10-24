package ru.avdeev.scheduleservice.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

public class InvalidTimeIntervalException extends ApiException{
    public InvalidTimeIntervalException(String message, Object... args) {
        super(HttpResponseStatus.BAD_REQUEST, message, args);
    }
}
