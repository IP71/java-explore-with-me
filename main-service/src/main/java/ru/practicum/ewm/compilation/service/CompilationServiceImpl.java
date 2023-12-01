package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationMapper;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.exception.CompilationNotFoundException;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> get(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> foundCompilations;
        if (pinned != null) {
            foundCompilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        } else {
            foundCompilations = compilationRepository.findAllBy(pageRequest);
        }
        log.info("{} compilations were found", foundCompilations.size());
        return CompilationMapper.toCompilationDto(foundCompilations);
    }

    @Override
    public CompilationDto getCompilationById(long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() -> new CompilationNotFoundException(id));
        log.info("Get compilation with id={}", id);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Set<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        Compilation compilation = compilationRepository.save(CompilationMapper.newCompilationDtoToCompilation(newCompilationDto, events));
        log.info("Created compilation with id={}, title={}", compilation.getId(), compilation.getTitle());
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilationById(long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() -> new CompilationNotFoundException(id));
        compilationRepository.deleteById(id);
        log.info("Compilation with id={} was deleted", id);
    }

    @Override
    @Transactional
    public CompilationDto update(long id, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() -> new CompilationNotFoundException(id));
        if (updateCompilationRequest.getEvents() != null) {
            Set<Event> newEvents = eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(newEvents);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        compilationRepository.save(compilation);
        log.info("Compilation with id={} was updated", id);
        return CompilationMapper.toCompilationDto(compilation);
    }
}
