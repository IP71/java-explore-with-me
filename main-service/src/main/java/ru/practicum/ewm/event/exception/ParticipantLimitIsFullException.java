package ru.practicum.ewm.event.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParticipantLimitIsFullException extends RuntimeException {
    public ParticipantLimitIsFullException() {
        super("The participant limit has been reached");
        log.error("The participant limit has been reached");
    }
}
