package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CompilationController {
    private final CompilationService compilationService;

    /**
     * Method for getting info about all compilations
     * @param pinned pinned, unpinned of all(if null)
     * @return Method returns info about compilations
     */
    @GetMapping("/compilations")
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> get(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                    @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return compilationService.get(pinned, from, size);
    }

    /**
     * Method for getting info about a compilation
     * @param compId id of compilation to get info about
     * @return Method return info about a compilation
     */
    @GetMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable long compId) {
        return compilationService.getCompilationById(compId);
    }

    /**
     * Method for creating a compilation
     * @param newCompilationDto data for creation
     * @return Method returns the created compilation
     */
    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.create(newCompilationDto);
    }

    /**
     * Method for deleting a compilation by id
     * @param compId id of compilation to delete
     */
    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@PathVariable long compId) {
        compilationService.deleteCompilationById(compId);
    }

    /**
     * Method for updating a compilation
     * @param compId id of compilation to update
     * @param updateCompilationRequest data for updating
     * @return Method returns the updated compilation
     */
    @PatchMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto update(@PathVariable long compId,
                                 @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return compilationService.update(compId, updateCompilationRequest);
    }
}
