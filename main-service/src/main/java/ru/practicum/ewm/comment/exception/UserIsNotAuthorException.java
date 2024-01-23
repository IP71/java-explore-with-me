package ru.practicum.ewm.comment.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserIsNotAuthorException extends RuntimeException {
    public UserIsNotAuthorException(long userId, long commentId) {
        super(String.format("User with id=%d is not the author of comment with id=%d", userId, commentId));
        log.error("User with id={} is not the author of comment with id={}", userId, commentId);
    }
}
