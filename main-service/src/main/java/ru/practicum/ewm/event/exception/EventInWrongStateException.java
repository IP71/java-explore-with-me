package ru.practicum.ewm.event.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventInWrongStateException extends RuntimeException {
    public EventInWrongStateException(String providedState, String eventState) {
        super(String.format("Cannot %s because it's not in the right state: %s", providedState, eventState));
        log.error("Cannot {} because it's not in the right state: {}", providedState, eventState);
    }
}
