package ru.practicum.ewm.event.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserIsNotInitiatorException extends RuntimeException {
    public UserIsNotInitiatorException(long userId, long eventId) {
        super(String.format("User with id=%d is not initiator of event with id=%d", userId, eventId));
        log.error("User with id={} is not initiator of event with id={}", userId, eventId);
    }
}
