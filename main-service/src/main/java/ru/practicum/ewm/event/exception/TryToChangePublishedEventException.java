package ru.practicum.ewm.event.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TryToChangePublishedEventException extends RuntimeException {
    public TryToChangePublishedEventException() {
        super("Only pending or canceled events can be changed");
        log.error("Only pending or canceled events can be changed");
    }
}
