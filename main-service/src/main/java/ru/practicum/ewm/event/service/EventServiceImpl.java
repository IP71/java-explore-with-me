package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.exception.CategoryNotFoundException;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.exception.*;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.exception.UserNotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatsClient client;

    /**
     * Method for getting info about events added by current user
     * @param userId id of current user
     * @return Method returns info about events
     */
    @Override
    public List<EventShortDto> getEventsAddedByUser(long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> foundEvents = eventRepository.findAllByInitiatorId(userId, pageRequest);
        log.info("Found {} events added by user with id={}", foundEvents.size(), userId);
        return EventMapper.toEventShortDto(foundEvents);
    }

    /**
     * Method for creating an event
     * @param userId id of user that creates an event
     * @param newEventDto data for creation
     * @return Method returns the created event
     */
    @Override
    @Transactional
    public EventFullDto create(long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Category category = checkEventCategory(newEventDto.getCategory());
        checkEventDate(LocalDateTime.parse(newEventDto.getEventDate(), FORMATTER));
        Location location = checkEventLocation(newEventDto.getLocation());
        Event event = eventRepository.save(EventMapper.newEventDtoToEvent(user, category, location, newEventDto));
        log.info("Created event with id={}, title={}", event.getId(), event.getTitle());
        return EventMapper.toEventFullDto(event);
    }

    /**
     * Method for getting an event by its creator
     * @param userId id of creator
     * @param eventId id of event
     * @return Method returns info about event
     */
    @Override
    public EventFullDto getEventByAuthor(long userId, long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        log.info("Get event with id={}", eventId);
        return EventMapper.toEventFullDto(event);
    }

    /**
     * Method for updating an event by its creator
     * @param userId id of creator
     * @param eventId id of event
     * @param request data for updating
     * @return Method returns the updated event
     */
    @Override
    @Transactional
    public EventFullDto updateEventByAuthor(long userId, long eventId, UpdateEventUserRequest request) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        if (event.getInitiator().getId() != userId) {
            throw new UserIsNotInitiatorException(userId, eventId);
        }
        if (event.getState().equals(Status.PUBLISHED)) {
            throw new TryToChangePublishedEventException();
        }
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            Category category = checkEventCategory(request.getCategory());
            event.setCategory(category);
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            checkEventDate(LocalDateTime.parse(request.getEventDate(), FORMATTER));
            event.setEventDate(LocalDateTime.parse(request.getEventDate(), FORMATTER));
        }
        if (request.getLocation() != null) {
            Location location = checkEventLocation(request.getLocation());
            event.setLocation(location);
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            if (event.getConfirmedRequests() > request.getParticipantLimit()) {
                throw new InvalidParticipantLimitException(request.getParticipantLimit(), event.getConfirmedRequests());
            }
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getStateAction() != null) {
            try {
                StateAction stateAction = StateAction.valueOf(request.getStateAction());
                if (stateAction.equals(StateAction.SEND_TO_REVIEW)) {
                    event.setState(Status.PENDING);
                } else {
                    event.setState(Status.CANCELED);
                }
            } catch (IllegalArgumentException e) {
                throw new InvalidStateActionException(request.getStateAction());
            }
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        event = eventRepository.save(event);
        log.info("Event with id={}, title={} was updated", event.getId(), event.getTitle());
        return EventMapper.toEventFullDto(event);
    }

    /**
     * Method for getting requests for event
     * @param userId id of current user
     * @param eventId id of event
     * @return Method returns info about requests
     */
    @Override
    public List<ParticipationRequestDto> getRequestsForEventOfCurrentUser(long userId, long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        if (event.getInitiator().getId() != userId) {
            throw new UserIsNotInitiatorException(userId, eventId);
        }
        List<ParticipationRequest> foundRequests = requestRepository.findAllByEventId(eventId);
        log.info("{} requests were found", foundRequests.size());
        return ParticipationRequestMapper.toParticipationRequestDto(foundRequests);
    }

    /**
     * Method for setting a status for requests
     * @param userId id of current user
     * @param eventId id of event
     * @param request data about requests
     * @return Method returns info about requests
     */
    @Override
    @Transactional
    public EventRequestStatusUpdateResult setStatusForRequestsByCurrentUser(long userId, long eventId, EventRequestStatusUpdateRequest request) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        if (event.getParticipantLimit() != 0L && event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ParticipantLimitIsFullException();
        }
        if (event.getInitiator().getId() != userId) {
            throw new UserIsNotInitiatorException(userId, eventId);
        }
        List<ParticipationRequest> foundRequests = requestRepository.findAllByIdIn(request.getRequestIds());
        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();
        try {
            if (Status.valueOf(request.getStatus()).equals(Status.REJECTED)) {
                for (ParticipationRequest participationRequest : foundRequests) {
                    if (!participationRequest.getStatus().equals(Status.PENDING)) {
                        throw new StatusNotPendingException();
                    }
                    rejectedRequests.add(participationRequest);
                    participationRequest.setStatus(Status.REJECTED);
                }
            } else if (Status.valueOf(request.getStatus()).equals(Status.CONFIRMED)) {
                for (ParticipationRequest participationRequest : foundRequests) {
                    if (!participationRequest.getStatus().equals(Status.PENDING)) {
                        throw new StatusNotPendingException();
                    }
                    if (event.getConfirmedRequests() < event.getParticipantLimit() || event.getParticipantLimit() == 0L) {
                        confirmedRequests.add(participationRequest);
                        participationRequest.setStatus(Status.CONFIRMED);
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1L);
                    } else {
                        rejectedRequests.add(participationRequest);
                        participationRequest.setStatus(Status.REJECTED);
                    }
                }
            } else {
                throw new StatusNotConfirmedOrRejectedException();
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException(request.getStatus());
        }
        eventRepository.save(event);
        requestRepository.saveAll(foundRequests);
        log.info("{} requests were confirmed, {} requests were rejected for event with id={}, title={}",
                confirmedRequests.size(), rejectedRequests.size(), event.getId(), event.getTitle());
        return new EventRequestStatusUpdateResult(ParticipationRequestMapper.toParticipationRequestDto(confirmedRequests),
                ParticipationRequestMapper.toParticipationRequestDto(rejectedRequests));
    }

    /**
     * Method for getting events by admin by multiple parameters
     * @return Method returns info about events
     */
    @Override
    public List<EventFullDto> getEventsByAdmin(AdminSearchParameters parameters) {
        List<Long> users = parameters.getUsers();
        List<Status> states = parameters.getStates();
        List<Long> categories = parameters.getCategories();
        LocalDateTime rangeStart = null;
        LocalDateTime rangeEnd = null;
        if (parameters.getRangeStart() != null) {
            rangeStart = LocalDateTime.parse(parameters.getRangeStart(), FORMATTER);
        }
        if (parameters.getRangeEnd() != null) {
            rangeEnd = LocalDateTime.parse(parameters.getRangeEnd(), FORMATTER);
        }
        PageRequest pageRequest = PageRequest.of(parameters.getFrom() / parameters.getSize(), parameters.getSize());
        List<Event> foundEvents = eventRepository.findAllByAdmin(users, states, categories, rangeStart, rangeEnd, pageRequest);
        log.info("Get events by admin: found {} events by users={}, states={}, categories={}, rangeStart={}, " +
                        "rangeEnd={}, from={}, size={}", foundEvents.size(), users, states, categories, rangeStart,
                rangeEnd, parameters.getFrom(), parameters.getSize());
        return EventMapper.toEventFullDto(foundEvents);
    }

    /**
     * Method for updating an event by admin
     * @param eventId id of event
     * @param request data for updating
     * @return Method returns the updated event
     */
    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        if (request.getStateAction() != null && StateAction.valueOf(request.getStateAction())
                .equals(StateAction.PUBLISH_EVENT) && !event.getState().equals(Status.PENDING)) {
            throw new EventInWrongStateException(request.getStateAction(), event.getState().toString());
        }
        if (request.getStateAction() != null && StateAction.valueOf(request.getStateAction())
                .equals(StateAction.REJECT_EVENT) && event.getState().equals(Status.PUBLISHED)) {
            throw new EventInWrongStateException(request.getStateAction(), event.getState().toString());
        }
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            Category category = checkEventCategory(request.getCategory());
            event.setCategory(category);
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            if (LocalDateTime.parse(request.getEventDate(), FORMATTER).isBefore(LocalDateTime.now())) {
                throw new BadNewDateException(request.getEventDate());
            }
            event.setEventDate(LocalDateTime.parse(request.getEventDate(), FORMATTER));
        }
        if (request.getLocation() != null) {
            Location location = checkEventLocation(request.getLocation());
            event.setLocation(location);
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            if (event.getConfirmedRequests() > request.getParticipantLimit()) {
                throw new InvalidParticipantLimitException(request.getParticipantLimit(), event.getConfirmedRequests());
            }
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getStateAction() != null) {
            StateAction stateAction = StateAction.valueOf(request.getStateAction());
            if (stateAction.equals(StateAction.PUBLISH_EVENT)) {
                if (LocalDateTime.now().plusHours(1).isAfter(event.getEventDate())) {
                    throw new TooLateToPublishException(event.getEventDate().format(FORMATTER));
                }
                event.setPublishedOn(LocalDateTime.now());
                event.setState(Status.PUBLISHED);
            } else {
                event.setState(Status.REJECTED);
            }
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        event = eventRepository.save(event);
        log.info("Event with id={}, title={} was updated by admin", event.getId(), event.getTitle());
        return EventMapper.toEventFullDto(event);
    }

    /**
     * Method for getting events by user by multiple parameters
     * @return Method returns info about events
     */
    @Override
    public List<EventShortDto> get(UserSearchParameters parameters, HttpServletRequest request) {
        String text = parameters.getText();
        List<Long> categories = parameters.getCategories();
        Boolean paid = parameters.getPaid();
        LocalDateTime rangeStart = null;
        LocalDateTime rangeEnd = null;
        if (parameters.getRangeStart() != null) {
            rangeStart = LocalDateTime.parse(parameters.getRangeStart(), FORMATTER);
        }
        if (parameters.getRangeEnd() != null) {
            rangeEnd = LocalDateTime.parse(parameters.getRangeEnd(), FORMATTER);
        }
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadDatesException(parameters.getRangeStart(), parameters.getRangeEnd());
        }
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
        }
        Boolean onlyAvailable = parameters.getOnlyAvailable();
        String sort = parameters.getSort();
        PageRequest pageRequest = PageRequest.of(parameters.getFrom() / parameters.getSize(), parameters.getSize());
        List<Event> foundEvents;
        if (sort.equals("EVENT_DATE")) {
            foundEvents = eventRepository.findAllByUserSortByEventDate(text, categories, paid, rangeStart, rangeEnd,
                    onlyAvailable, pageRequest);
        } else {
            foundEvents = eventRepository.findAllByUserSortByViews(text, categories, paid, rangeStart, rangeEnd,
                    onlyAvailable, pageRequest);
        }
        log.info("Get events by user: found {} events by text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, " +
                        "onlyAvailable={}, sort={}, from={}, size={}", foundEvents.size(), text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, parameters.getFrom(), parameters.getSize());
        client.addEndpointHit("ewm", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now().format(FORMATTER));
        return EventMapper.toEventShortDto(foundEvents);
    }

    /**
     * Method for getting event by id
     * @param id id of event
     * @return Method returns info about an event
     */
    @Override
    public EventFullDto getEventById(long id, HttpServletRequest request) {
        Optional<Event> optionalEvent = eventRepository.findByIdAndState(id, Status.PUBLISHED);
        Event event = optionalEvent.orElseThrow(() -> new EventNotFoundException(id));
        if (client.checkIfIpIsUnique(request.getRequestURI(), request.getRemoteAddr())) {
            event.setViews(event.getViews() + 1L);
            event = eventRepository.save(event);
        }
        log.info("Get event with id={} by user", id);
        client.addEndpointHit("ewm", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now().format(FORMATTER));
        return EventMapper.toEventFullDto(event);
    }

    /**
     * Method checks if event date is valid
     */
    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadEventDateException(eventDate.format(FORMATTER));
        }
    }

    /**
     * Method checks if event category exists
     */
    private Category checkEventCategory(long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    /**
     * Method checks if event location is saved, and if not, saves it
     */
    private Location checkEventLocation(Location location) {
        Optional<Location> optionalLocation = locationRepository
                .findFirst1ByLatIsAndLonIs(location.getLat(), location.getLon());
        return optionalLocation.orElseGet(() -> locationRepository.save(location));
    }
}
