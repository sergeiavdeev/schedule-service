package ru.avdeev.scheduleservice.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ResourceNotFoundException extends ApiException{

    public ResourceNotFoundException(String message, Object... args) {
        super(HttpResponseStatus.NOT_FOUND, message, args);
    }
}
