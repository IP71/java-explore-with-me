package ru.practicum.ewm.location.dto;

import ru.practicum.ewm.location.model.Location;

public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
