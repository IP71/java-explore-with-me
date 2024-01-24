package ru.practicum.ewm.comment.dto;

import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Comment newCommentDtoToComment(Event event, User author, NewCommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .event(event)
                .author(author)
                .created(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    public static CommentDto commentToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .eventId(comment.getEvent().getId())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .created(comment.getCreated().format(FORMATTER))
                .lastUpdated(comment.getLastUpdated().format(FORMATTER))
                .build();
    }

    public static List<CommentDto> commentToCommentDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::commentToCommentDto)
                .collect(Collectors.toList());
    }
}
