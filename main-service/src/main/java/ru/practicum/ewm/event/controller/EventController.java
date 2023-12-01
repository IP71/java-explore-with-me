package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.AdminSearchParameters;
import ru.practicum.ewm.event.model.UserSearchParameters;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class EventController {
    private final EventService eventService;

    @GetMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsAddedByUser(@PathVariable long userId,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return eventService.getEventsAddedByUser(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable long userId,
                               @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.create(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByAuthor(@PathVariable long userId,
                                         @PathVariable long eventId) {
        return eventService.getEventByAuthor(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAuthor(@PathVariable long userId,
                                            @PathVariable long eventId,
                                            @Valid @RequestBody UpdateEventUserRequest request) {
        return eventService.updateEventByAuthor(userId, eventId, request);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsForEventOfCurrentUser(@PathVariable long userId,
                                                                          @PathVariable long eventId) {
        return eventService.getRequestsForEventOfCurrentUser(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult setStatusForRequestsByCurrentUser(@PathVariable long userId,
                                                                            @PathVariable long eventId,
                                                                            @RequestBody EventRequestStatusUpdateRequest request) {
        return eventService.setStatusForRequestsByCurrentUser(userId, eventId, request);
    }

    @GetMapping("/admin/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEventsByAdmin(@RequestParam(name = "users", required = false) List<Long> users,
                                               @RequestParam(name = "states", required = false) List<String> states,
                                               @RequestParam(name = "categories", required = false) List<Long> categories,
                                               @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                               @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return eventService.getEventsByAdmin(new AdminSearchParameters(users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/admin/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@PathVariable long eventId,
                                           @Valid @RequestBody UpdateEventAdminRequest request) {
        return eventService.updateEventByAdmin(eventId, request);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> get(@RequestParam(name = "text", required = false) String text,
                                   @RequestParam(name = "categories", required = false) List<Long> categories,
                                   @RequestParam(name = "paid", required = false) boolean paid,
                                   @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                   @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                   @RequestParam(name = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
                                   @RequestParam(name = "sort", defaultValue = "VIEWS") String sort,
                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                   @Positive @RequestParam(name = "size", defaultValue = "10") int size,
                                   HttpServletRequest request) {
        return eventService.get(new UserSearchParameters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size), request);
    }

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable long id,
                                     HttpServletRequest request) {
        return eventService.getEventById(id, request);
    }
}
