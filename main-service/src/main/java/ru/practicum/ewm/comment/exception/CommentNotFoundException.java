package ru.practicum.ewm.comment.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(long id) {
        super(String.format("Comment with id=%d was not found", id));
        log.error("Comment with id={} was not found", id);
    }
}
