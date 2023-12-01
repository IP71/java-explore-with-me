package ru.practicum.ewm.event.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidParticipantLimitException extends RuntimeException {
    public InvalidParticipantLimitException(long limit, long number) {
        super(String.format("Provided participant limit (%d) cannot be less than number of confirmed requests (%d)", limit, number));
        log.error("Provided participant limit ({}) cannot be less than number of confirmed requests ({})", limit, number);
    }
}
