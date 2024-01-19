package ru.practicum.ewm.event.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException(String status) {
        super(String.format("Invalid status=%s was provided", status));
        log.error("Invalid status={} was provided", status);
    }
}
