package ru.practicum.ewm.category.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CategoryNotEmptyException extends RuntimeException {
    public CategoryNotEmptyException(String name) {
        super("The category is not empty");
        log.error("The category '{}' is not empty", name);
    }
}
