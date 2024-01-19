package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequestsForCurrentUser(long userId);

    ParticipationRequestDto create(long userId, long eventId);

    ParticipationRequestDto cancelRequestForCurrentUser(long userId, long requestId);
}
