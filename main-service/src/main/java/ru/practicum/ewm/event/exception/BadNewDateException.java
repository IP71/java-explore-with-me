package ru.practicum.ewm.event.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BadNewDateException extends RuntimeException {
    public BadNewDateException(String date) {
        super(String.format("New event date=%s cannot be earlier than current date&time", date));
        log.error("New event date={} cannot be earlier than current date&time", date);
    }
}
