package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.AdminSearchParameters;
import ru.practicum.ewm.event.model.UserSearchParameters;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    List<EventShortDto> getEventsAddedByUser(long userId, int from, int size);

    EventFullDto create(long userId, NewEventDto newEventDto);

    EventFullDto getEventByAuthor(long userId, long eventId);

    EventFullDto updateEventByAuthor(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestsForEventOfCurrentUser(long userId, long eventId);

    EventRequestStatusUpdateResult setStatusForRequestsByCurrentUser(long userId, long eventId,
                                                                     EventRequestStatusUpdateRequest request);

    List<EventFullDto> getEventsByAdmin(AdminSearchParameters adminSearchParameters);

    EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> get(UserSearchParameters userSearchParameters, HttpServletRequest request);

    EventFullDto getEventById(long id, HttpServletRequest request);
}
