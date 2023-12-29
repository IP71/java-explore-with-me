package ru.practicum.ewm.event.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidStateActionException extends RuntimeException {
    public InvalidStateActionException(String stateAction) {
        super(String.format("Invalid state action=%s was provided", stateAction));
        log.error("Invalid state action={} was provided", stateAction);
    }
}
