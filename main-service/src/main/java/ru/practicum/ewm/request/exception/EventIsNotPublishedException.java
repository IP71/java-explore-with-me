package ru.practicum.ewm.request.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventIsNotPublishedException extends RuntimeException {
    public EventIsNotPublishedException(long eventId) {
        super(String.format("Event with id=%d is not published", eventId));
        log.error("Event with id={} is not published", eventId);
    }
}
