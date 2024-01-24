package ru.practicum.ewm.comment.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventNotPublishedException extends RuntimeException {
    public EventNotPublishedException(long id) {
        super(String.format("Event with id=%d is not published, cannot post comment", id));
        log.error("Event with id={} is not published, cannot post comment", id);
    }
}
