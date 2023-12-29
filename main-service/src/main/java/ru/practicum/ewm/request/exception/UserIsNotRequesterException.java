package ru.practicum.ewm.request.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserIsNotRequesterException extends RuntimeException {
    public UserIsNotRequesterException(long userId, long requestId) {
        super(String.format("User with id=%d is not author of request with id=%d", userId, requestId));
        log.error("User with id={} is not author of request with id={}", userId, requestId);
    }
}
