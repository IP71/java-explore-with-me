package ru.practicum.ewm.event.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BadDatesException extends RuntimeException {
    public BadDatesException(String startDate, String endDate) {
        super(String.format("Event start date=%s is later than end date=%s", startDate, endDate));
        log.error("Event start date ={} is later than end date={}", startDate, endDate);
    }
}
