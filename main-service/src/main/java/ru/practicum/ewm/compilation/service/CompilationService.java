package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> get(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(long id);

    CompilationDto create(NewCompilationDto newCompilationDto);

    void deleteCompilationById(long id);

    CompilationDto update(long id, UpdateCompilationRequest updateCompilationRequest);
}
