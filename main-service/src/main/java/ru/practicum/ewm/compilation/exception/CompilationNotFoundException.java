package ru.practicum.ewm.compilation.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompilationNotFoundException extends RuntimeException {
    public CompilationNotFoundException(long id) {
        super(String.format("Compilation with id=%d was not found", id));
        log.error("Compilation with id={} was not found", id);
    }
}
