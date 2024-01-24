package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentMapper;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.exception.CommentNotFoundException;
import ru.practicum.ewm.comment.exception.EventNotPublishedException;
import ru.practicum.ewm.comment.exception.UserIsNotAuthorException;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.exception.EventNotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Status;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.user.exception.UserNotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    /**
     * Method for creating a comment
     * @param eventId id of event to post comment on
     * @param userId id of the poster
     * @param newCommentDto data for creation
     * @return Method returns the created comment
     */
    @Override
    @Transactional
    public CommentDto create(NewCommentDto newCommentDto, long eventId, long userId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (!event.getState().equals(Status.PUBLISHED)) {
            throw new EventNotPublishedException(eventId);
        }
        Comment comment = CommentMapper.newCommentDtoToComment(event, user, newCommentDto);
        comment = commentRepository.save(comment);
        log.info("Comment with id={} was created", comment.getId());
        return CommentMapper.commentToCommentDto(comment);
    }

    /**
     * Method for updating a comment
     * @param userId id of user that wants to update
     * @param commentId id of comment to update
     * @param newCommentDto data for updating
     * @return Method returns the updated comment
     */
    @Override
    @Transactional
    public CommentDto update(NewCommentDto newCommentDto, long userId, long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (userId != comment.getAuthor().getId()) {
            throw new UserIsNotAuthorException(userId, commentId);
        }
        comment.setText(newCommentDto.getText());
        comment.setLastUpdated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        log.info("Comment with id={} was updated", commentId);
        return CommentMapper.commentToCommentDto(comment);
    }

    /**
     * Method for getting a comment by id
     * @param commentId if of a comment
     * @return Method returns a comment
     */
    @Override
    public CommentDto getCommentById(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
        log.info("Get comment with id={}", commentId);
        return CommentMapper.commentToCommentDto(comment);
    }

    /**
     * Method for deleting a comment by author
     * @param userId id of author
     * @param commentId id of a comment to delete
     */
    @Override
    @Transactional
    public void deleteCommentById(long userId, long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
        if (userId != comment.getAuthor().getId()) {
            throw new UserIsNotAuthorException(userId, commentId);
        }
        commentRepository.deleteById(commentId);
        log.info("Comment with id={} was deleted", commentId);
    }

    /**
     * Method for getting all comments on event by its id
     * @param eventId id of event
     * @return Method returns all comments
     */
    @Override
    public List<CommentDto> getCommentsByEventId(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        if (!event.getState().equals(Status.PUBLISHED)) {
            throw new EventNotPublishedException(eventId);
        }
        List<Comment> foundComments = commentRepository.findAllByEventId(eventId);
        log.info("{} comments were found for event with id={}", foundComments.size(), eventId);
        return CommentMapper.commentToCommentDto(foundComments);
    }

    /**
     * Method for getting all comments of a user by his id
     * @param userId id of user
     * @return Method returns all comments of the user
     */
    @Override
    public List<CommentDto> getCommentsByUserId(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<Comment> foundComments = commentRepository.findAllByAuthorId(userId);
        log.info("{} comments were found for user with id={}", foundComments.size(), userId);
        return CommentMapper.commentToCommentDto(foundComments);
    }

    /**
     * Method for deleting a comment by admin
     * @param commentId id of a comment to delete
     */
    @Override
    @Transactional
    public void deleteCommentByAdmin(long commentId) {
        commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(commentId));
        commentRepository.deleteById(commentId);
        log.info("Comment with id={} was deleted by admin", commentId);
    }

    /**
     * Method for getting comments by text request
     * @param text string query to search comments
     * @param from number of page to get events from
     * @param size number of events to get
     * @return Method returns found comments
     */
    @Override
    public List<CommentDto> searchComments(String text, int from, int size) {
        List<Comment> foundComments = commentRepository.search(text, PageRequest.of(from / size, size));
        log.info("Found {} comments by text={}", foundComments.size(), text);
        return CommentMapper.commentToCommentDto(foundComments);
    }
}
