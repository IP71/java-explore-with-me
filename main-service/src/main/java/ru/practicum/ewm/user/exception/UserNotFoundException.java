package ru.practicum.ewm.user.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        super(String.format("User with id=%d was not found", id));
        log.error("User with id={} was not found", id);
    }
}
