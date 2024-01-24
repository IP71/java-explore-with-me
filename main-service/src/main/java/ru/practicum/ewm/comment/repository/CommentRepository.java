package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventId(long eventId);

    List<Comment> findAllByAuthorId(long userId);

    @Query("SELECT c From Comment c " +
            "WHERE (lower(c.text) like lower(concat('%', ?1, '%'))) " +
            "ORDER BY c.lastUpdated DESC")
    List<Comment> search(String text, Pageable pageable);
}
