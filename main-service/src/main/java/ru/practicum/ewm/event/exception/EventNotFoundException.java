package ru.practicum.ewm.event.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(long id) {
        super(String.format("Event with id=%d was not found", id));
        log.error("Event with id={} was not found", id);
    }
}
