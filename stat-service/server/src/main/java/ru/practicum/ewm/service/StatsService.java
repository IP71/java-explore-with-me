package ru.practicum.ewm.service;

import ru.practicum.ewm.model.EndpointHitDto;
import ru.practicum.ewm.model.EndpointHitResponse;
import ru.practicum.ewm.model.ViewStatsDto;

import java.util.List;

public interface StatsService {
    EndpointHitResponse addEndpointHit(EndpointHitDto endpointHit);

    List<ViewStatsDto> getStats(String start, String end, String[] uris, boolean unique);

    Boolean checkIfIpIsUnique(String uri, String ip);
}
