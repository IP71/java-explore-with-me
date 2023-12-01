package ru.practicum.ewm.request.dto;

import ru.practicum.ewm.request.model.ParticipationRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .eventId(participationRequest.getEvent().getId())
                .requesterId(participationRequest.getRequester().getId())
                .status(participationRequest.getStatus())
                .created(participationRequest.getCreated())
                .build();
    }

    public static List<ParticipationRequestDto> toParticipationRequestDto(List<ParticipationRequest> participationRequests) {
        return participationRequests.stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }
}
