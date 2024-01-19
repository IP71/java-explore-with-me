package ru.practicum.ewm.event.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatusNotConfirmedOrRejectedException extends RuntimeException {
    public StatusNotConfirmedOrRejectedException() {
        super("Status has to be CONFIRMED or REJECTED");
        log.error("Status has to be CONFIRMED or REJECTED");
    }
}
