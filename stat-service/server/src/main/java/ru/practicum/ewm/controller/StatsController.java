package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.EndpointHitDto;
import ru.practicum.ewm.model.EndpointHitResponse;
import ru.practicum.ewm.model.ViewStatsDto;
import ru.practicum.ewm.service.StatsService;

import javax.validation.Valid;
import java.util.List;

/**
 * Контроллер для сохранения и получения статистики посещений сайта
 */

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    /**
     * Метод сохраняет данные о посещении эндпоинта
     *
     * @param endpointHitDto объект EndpointHitDto, переданный в теле запроса
     * @return В случае успешного сохранения информации метод сообщает об этом
     */
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitResponse addEndpointHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        return statsService.addEndpointHit(endpointHitDto);
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
    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getStats(@RequestParam(name = "start") String start,
                                       @RequestParam(name = "end") String end,
                                       @RequestParam(name = "uris", required = false) String[] uris,
                                       @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }

    /**
     * Метод проверяет уникальность EndpointHit
     *
     * @param uri - uri
     * @param ip - ip
     * @return Метод возвращает true, если запрос уникальный
     */
    @GetMapping("/unique")
    @ResponseStatus(HttpStatus.OK)
    public Boolean checkIfIpIsUnique(@RequestParam(name = "uri") String uri,
                                     @RequestParam(name = "ip") String ip) {
        return statsService.checkIfIpIsUnique(uri, ip);
    }
}
