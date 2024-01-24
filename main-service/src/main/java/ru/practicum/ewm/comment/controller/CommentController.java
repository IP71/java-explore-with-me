package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CommentController {
    private final CommentService commentService;

    /**
     * Method for creating a comment
     * @param eventId id of event to post comment on
     * @param userId id of the poster
     * @param newCommentDto data for creation
     * @return Method returns the created comment
     */
    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable long eventId,
                             @RequestParam(name = "userId") long userId,
                             @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.create(newCommentDto, eventId, userId);
    }

    /**
     * Method for updating a comment
     * @param userId id of user that wants to update
     * @param commentId id of comment to update
     * @param newCommentDto data for updating
     * @return Method returns the updated comment
     */
    @PatchMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto update(@PathVariable long userId,
                             @PathVariable long commentId,
                             @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.update(newCommentDto, userId, commentId);
    }

    /**
     * Method for getting a comment by id
     * @param commentId if of a comment
     * @return Method returns a comment
     */
    @GetMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(@PathVariable long commentId) {
        return commentService.getCommentById(commentId);
    }

    /**
     * Method for deleting a comment by author
     * @param userId id of author
     * @param commentId id of a comment to delete
     */
    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById(@PathVariable long userId,
                                  @PathVariable long commentId) {
        commentService.deleteCommentById(userId, commentId);
    }

    /**
     * Method for getting all comments on event by its id
     * @param eventId id of event
     * @return Method returns all comments
     */
    @GetMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByEventId(@PathVariable long eventId) {
        return commentService.getCommentsByEventId(eventId);
    }

    /**
     * Method for getting all comments of a user by his id
     * @param userId id of user
     * @return Method returns all comments of the user
     */
    @GetMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByUserId(@PathVariable long userId) {
        return commentService.getCommentsByUserId(userId);
    }

    /**
     * Method for deleting a comment by admin
     * @param commentId id of a comment to delete
     */
    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }

    /**
     * Method for getting comments by text request
     * @param text string query to search comments
     * @param from number of page to get events from
     * @param size number of events to get
     * @return Method returns found comments
     */
    @GetMapping("/comments/search")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> searchComments(@NotBlank @RequestParam(name = "text") String text,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return commentService.searchComments(text, from, size);
    }
}
