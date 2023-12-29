package ru.practicum.ewm.event.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatusNotPendingException extends RuntimeException {
    public StatusNotPendingException() {
        super("Request must have status PENDING");
        log.error("Request must have status PENDING");
    }
}
