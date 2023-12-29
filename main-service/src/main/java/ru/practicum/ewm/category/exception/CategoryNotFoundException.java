package ru.practicum.ewm.category.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(long id) {
        super(String.format("Category with id=%d was not found", id));
        log.error("Category with id={} was not found", id);
    }
}
