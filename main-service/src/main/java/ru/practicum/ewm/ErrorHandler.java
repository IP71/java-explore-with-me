package ru.practicum.ewm;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.category.exception.CategoryNotEmptyException;
import ru.practicum.ewm.category.exception.CategoryNotFoundException;
import ru.practicum.ewm.compilation.exception.CompilationNotFoundException;
import ru.practicum.ewm.event.exception.*;
import ru.practicum.ewm.request.exception.*;
import ru.practicum.ewm.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice("ru.practicum.ewm")
public class ErrorHandler {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler({CategoryNotFoundException.class, CompilationNotFoundException.class,
            EventNotFoundException.class, RequestNotFoundException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleResourceNotFoundException(Throwable e) {
        List<String> errors = new ArrayList<>();
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            errors.add(stackTraceElement + "\n");
        }
        return ApiError.builder()
                .errors(errors)
                .message(e.getMessage())
                .reason("The required object was not found")
                .status(HttpStatus.NOT_FOUND.toString())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler({org.hibernate.exception.ConstraintViolationException.class, CategoryNotEmptyException.class,
    BadEventDateException.class, UserIsNotInitiatorException.class, TryToChangePublishedEventException.class,
    InvalidParticipantLimitException.class, ParticipantLimitIsFullException.class, StatusNotPendingException.class,
    StatusNotConfirmedOrRejectedException.class, EventInWrongStateException.class, TooLateToPublishException.class,
    RequestIsAlreadyExistsException.class, UserIsInitiatorException.class, EventIsNotPublishedException.class,
    UserIsNotRequesterException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(Throwable e) {
        List<String> errors = new ArrayList<>();
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            errors.add(stackTraceElement + "\n");
        }
        return ApiError.builder()
                .errors(errors)
                .message(e.getMessage())
                .reason("For the requested operation the conditions are not met")
                .status(HttpStatus.CONFLICT.toString())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(Throwable e) {
        List<String> errors = new ArrayList<>();
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            errors.add(stackTraceElement + "\n");
        }
        return ApiError.builder()
                .errors(errors)
                .message(e.getMessage())
                .reason("Incorrectly made request")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }
}
