package ru.practicum.ewm.request.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestIsAlreadyExistsException extends RuntimeException {
    public RequestIsAlreadyExistsException(long userId, long eventId) {
        super(String.format("User with id=%d already has request for event with id=%d", userId, eventId));
        log.error("User with id={} already has request for event with id={}", userId, eventId);
    }
}
