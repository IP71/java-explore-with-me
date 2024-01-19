package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestService requestService;

    /**
     * Method for getting requests by current user
     * @param userId id of current user
     * @return Method returns info about requests
     */
    @GetMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsForCurrentUser(@PathVariable long userId) {
        return requestService.getRequestsForCurrentUser(userId);
    }

    /**
     * Method for creating a request
     * @param userId id of current user
     * @param eventId id of event
     * @return Method returns the created request
     */
    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable long userId,
                                          @RequestParam(name = "eventId") long eventId) {
        return requestService.create(userId, eventId);
    }

    /**
     * Method for canceling a request
     * @param userId id of current user
     * @param requestId id of request
     * @return Method returns the canceled request
     */
    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequestForCurrentUser(@PathVariable long userId,
                                                               @PathVariable long requestId) {
        return requestService.cancelRequestForCurrentUser(userId, requestId);
    }
}
