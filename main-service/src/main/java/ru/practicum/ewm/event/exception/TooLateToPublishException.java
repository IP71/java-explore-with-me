package ru.practicum.ewm.event.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TooLateToPublishException extends RuntimeException {
    public TooLateToPublishException(String date) {
        super(String.format("Event Date: %s. It is too late to publish this event now", date));
        log.error("Event Date: {}. It is too late to publish this event now", date);
    }
}
