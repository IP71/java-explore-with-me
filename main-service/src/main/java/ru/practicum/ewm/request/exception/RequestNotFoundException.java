package ru.practicum.ewm.request.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(long id) {
        super(String.format("Request with id=%d was not found", id));
        log.error("Request with id={} was not found", id);
    }
}
