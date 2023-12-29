package ru.practicum.ewm.compilation.dto;

import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.model.Event;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .events(Set.copyOf(EventMapper.toEventShortDto(compilation.getEvents())))
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static List<CompilationDto> toCompilationDto(List<Compilation> compilations) {
        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    public static Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto, Set<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }
}
