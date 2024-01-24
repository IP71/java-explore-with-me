package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto create(NewCommentDto newCommentDto, long eventId, long userId);

    CommentDto update(NewCommentDto newCommentDto, long userId, long commentId);

    CommentDto getCommentById(long commentId);

    void deleteCommentById(long userId, long commentId);

    List<CommentDto> getCommentsByEventId(long eventId);

    List<CommentDto> getCommentsByUserId(long userId);

    void deleteCommentByAdmin(long commentId);

    List<CommentDto> searchComments(String text, int from, int size);
}
