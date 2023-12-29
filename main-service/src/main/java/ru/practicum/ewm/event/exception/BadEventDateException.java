package ru.practicum.ewm.event.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BadEventDateException extends RuntimeException {
    public BadEventDateException(String eventDate) {
        super(String.format("Field: eventDate. Error: should have future date. Value: %s", eventDate));
        log.info("Event date cannot be earlier than current time + 2 hours, value={} is invalid", eventDate);
    }
}
