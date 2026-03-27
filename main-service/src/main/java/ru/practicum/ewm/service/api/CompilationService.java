package ru.practicum.ewm.service.api;

import ru.practicum.ewm.dto.request.NewCompilationDto;
import ru.practicum.ewm.dto.request.UpdateCompilationRequest;
import ru.practicum.ewm.dto.response.CompilationDto;

import java.util.List;

public interface CompilationService {

    // публичные методы
    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(Long compId);

    // методы для админа
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest);

    void deleteCompilation(Long compId);
}