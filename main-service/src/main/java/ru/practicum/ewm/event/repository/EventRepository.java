package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByCategoryId(long id);

    Set<Event> findAllByIdIn(List<Long> ids);

    List<Event> findAllByInitiatorId(long userId, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (e.initiator.id IN ?1 OR ?1 IS NULL) " +
            "AND (e.state IN ?2 OR ?2 IS NULL) " +
            "AND (e.category.id IN ?3 OR ?3 IS NULL) " +
            "AND (e.eventDate > ?4 OR ?4 IS NULL) " +
            "AND (e.eventDate < ?5 OR ?5 IS NULL) " +
            "ORDER BY e.eventDate ASC")
    List<Event> findAllByAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart,
                               LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE ((lower(e.annotation) like lower(concat('%', ?1, '%'))) " +
            "OR (lower(e.description) like lower(concat('%', ?1, '%')))) " +
            "OR (?1 IS NULL) " +
            "AND (e.category.id IN ?2 OR ?2 IS NULL) " +
            "AND (e.paid IS ?3 OR ?3 IS NULL) " +
            "AND (e.eventDate > ?4 OR ?4 IS NULL) " +
            "AND (e.eventDate < ?5 OR ?5 IS NULL) " +
            "AND (e.confirmedRequests < e.participantLimit OR e.participantLimit = 0 OR ?6 = false) " +
            "AND (e.state = 'PUBLISHED') " +
            "ORDER BY e.eventDate ASC")
    List<Event> findAllByUserSortByEventDate(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                              LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE ((lower(e.annotation) like lower(concat('%', ?1, '%'))) " +
            "OR (lower(e.description) like lower(concat('%', ?1, '%')))) " +
            "OR (?1 IS NULL) " +
            "AND (e.category.id IN ?2 OR ?2 IS NULL) " +
            "AND (e.paid IS ?3 OR ?3 IS NULL) " +
            "AND (e.eventDate > ?4 OR ?4 IS NULL) " +
            "AND (e.eventDate < ?5 OR ?5 IS NULL) " +
            "AND (e.confirmedRequests < e.participantLimit OR e.participantLimit = 0 OR ?6 = false) " +
            "AND (e.state = 'PUBLISHED') " +
            "ORDER BY e.views DESC")
    List<Event> findAllByUserSortByViews(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable);

    Optional<Event> findByIdAndState(long id, Status status);
}
