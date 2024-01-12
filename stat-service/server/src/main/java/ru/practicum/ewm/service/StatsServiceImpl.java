package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsRepository statsRepository;

    /**
     * Метод сохраняет данные о посещении эндпоинта
     *
     * @param endpointHitDto объект EndpointHitDto, переданный в теле запроса
     * @return В случае успешного сохранения информации метод сообщает об этом
     */
    @Override
    @Transactional
    public EndpointHitResponse addEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit savedHit = statsRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
        log.info("EndpointHit {} создан", savedHit);
        return new EndpointHitResponse("Информация сохранена");
    }

    /**
     * Метод возвращает статистику посещений по определенным uri
     *
     * @param start  - начало интересующего временного промежутка
     * @param end    - конец интересующего временного промежутка
     * @param uris   - массив uri
     * @param unique - учитывать ли посещения с одного ip как разные
     * @return Метод возвращает статистику посещений
     */
    @Override
    public List<ViewStatsDto> getStats(String start, String end, String[] uris, boolean unique) {
        List<ViewStatsDto> stats = new ArrayList<>();
        List<EndpointHit> foundHits;
        if (uris == null) {
            foundHits = statsRepository.findAllByTimestampIsAfterAndTimestampIsBefore(
                    LocalDateTime.parse(start, FORMATTER), LocalDateTime.parse(end, FORMATTER));
        } else {
            foundHits = statsRepository.findAllByTimestampIsAfterAndTimestampIsBeforeAndUriIn(LocalDateTime
                    .parse(start, FORMATTER), LocalDateTime.parse(end, FORMATTER), List.of(uris));
        }
        if (unique) {
            foundHits = foundHits.stream()
                    .distinct()
                    .collect(Collectors.toList());
        }
        for (EndpointHit hit : foundHits) {
            ViewStatsDto stat = new ViewStatsDto(hit.getApp(), hit.getUri(), 1);
            if (stats.contains(stat)) {
                for (ViewStatsDto foundStat : stats) {
                    if (foundStat.equals(stat)) {
                        foundStat.addHit();
                    }
                }
            } else {
                stats.add(stat);
            }
        }
        stats.sort((a, b) -> b.getHits().compareTo(a.getHits()));
        log.info("Найдено {} объектов по запросу start={}, end={}, uris={}, unique={}", stats.size(), start, end, uris, unique);
        return stats;
    }

    /**
     * Метод проверяет уникальность EndpointHit
     *
     * @param uri - uri
     * @param ip - ip
     * @return Метод возвращает true, если запрос уникальный
     */
    @Override
    public Boolean checkIfIpIsUnique(String uri, String ip) {
        Optional<EndpointHit> foundHit = statsRepository.findFirst1ByUriAndIp(uri, ip);
        return foundHit.isEmpty();
    }
}
