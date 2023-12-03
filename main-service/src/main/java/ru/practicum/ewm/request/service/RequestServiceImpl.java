package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.exception.EventNotFoundException;
import ru.practicum.ewm.event.exception.ParticipantLimitIsFullException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Status;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.ParticipationRequestMapper;
import ru.practicum.ewm.request.exception.*;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.exception.UserNotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    /**
     * Method for getting requests by current user
     * @param userId id of current user
     * @return Method returns info about requests
     */
    @Override
    public List<ParticipationRequestDto> getRequestsForCurrentUser(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<ParticipationRequest> foundRequests = requestRepository.findAllByRequesterId(userId);
        log.info("Found {} requests for user with id={}", foundRequests.size(), userId);
        return ParticipationRequestMapper.toParticipationRequestDto(foundRequests);
    }

    /**
     * Method for creating a request
     * @param userId id of current user
     * @param eventId id of event
     * @return Method returns the created request
     */
    @Override
    public ParticipationRequestDto create(long userId, long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        Optional<ParticipationRequest> optionalRequest = requestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (optionalRequest.isPresent()) {
            throw new RequestIsAlreadyExistsException(userId, eventId);
        }
        if (event.getInitiator().getId() == userId) {
            throw new UserIsInitiatorException(userId, eventId);
        }
        if (!event.getState().equals(Status.PUBLISHED)) {
            throw new EventIsNotPublishedException(eventId);
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit() && event.getParticipantLimit() != 0L) {
            throw new ParticipantLimitIsFullException();
        }
        Status status;
        if (event.getRequestModeration()) {
            status = Status.PENDING;
        } else {
            status = Status.CONFIRMED;
        }
        ParticipationRequest request = requestRepository.save(ParticipationRequest.builder()
                .event(event)
                .requester(user)
                .status(status)
                .created(LocalDateTime.now())
                .build());
        log.info("Created request: {}", request);
        return ParticipationRequestMapper.toParticipationRequestDto(request);
    }

    /**
     * Method for canceling a request
     * @param userId id of current user
     * @param requestId id of request
     * @return Method returns the canceled request
     */
    @Override
    public ParticipationRequestDto cancelRequestForCurrentUser(long userId, long requestId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        ParticipationRequest participationRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));
        if (userId != participationRequest.getRequester().getId()) {
            throw new UserIsNotRequesterException(userId, requestId);
        }
        participationRequest.setStatus(Status.CANCELED);
        requestRepository.save(participationRequest);
        log.info("Request with id={} was canceled", requestId);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }
}
