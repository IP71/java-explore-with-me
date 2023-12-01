package ru.practicum.ewm.request.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserIsInitiatorException extends RuntimeException {
    public UserIsInitiatorException(long userId, long eventId) {
        super(String.format("User with id=%d is initiator of event with id=%d", userId, eventId));
        log.error("User with id={} is initiator of event with id={}", userId, eventId);
    }
}
