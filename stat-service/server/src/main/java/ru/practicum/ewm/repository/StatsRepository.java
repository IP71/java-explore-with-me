package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    List<EndpointHit> findAllByTimestampIsAfterAndTimestampIsBeforeAndUriIn(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<EndpointHit> findAllByTimestampIsAfterAndTimestampIsBefore(LocalDateTime start, LocalDateTime end);
}
