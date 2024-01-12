package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    List<EndpointHit> findAllByTimestampIsAfterAndTimestampIsBeforeAndUriIn(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<EndpointHit> findAllByTimestampIsAfterAndTimestampIsBefore(LocalDateTime start, LocalDateTime end);

    Optional<EndpointHit> findFirst1ByUriAndIp(String uri, String ip);
}
