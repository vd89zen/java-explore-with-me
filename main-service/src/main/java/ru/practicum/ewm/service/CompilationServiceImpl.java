package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.request.NewCompilationDto;
import ru.practicum.ewm.dto.request.UpdateCompilationRequest;
import ru.practicum.ewm.dto.response.CompilationDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.utils.NotFound;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.service.api.CommentService;
import ru.practicum.ewm.service.api.CompilationService;
import ru.practicum.ewm.service.api.EventService;
import ru.practicum.ewm.service.api.ValidationService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventService eventService;
    private final ValidationService validationService;
    private final CommentService commentService;

    // публичные методы
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("Получаем подборки - pinned: {}, from: {}, size: {}", pinned, from, size);

        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);
        List<Compilation> compilations = compilationRepository.findAllWithFilters(pinned, pageable).getContent();

        List<Event> allEvents = compilations.stream()
                .flatMap(compilation -> compilation.getEvents().stream())
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Long> viewsMap = eventService.getViewStatsForEvents(allEvents);
        List<Long> eventIds = allEvents.stream().map(Event::getId).toList();
        Map<Long, Long> commentsMap = commentService.getCountNumberOfCommentsForEvent(eventIds);

        return compilations.stream()
                .map(comp -> CompilationMapper.toCompilationDto(comp, viewsMap, commentsMap))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.info("Получаем подборку id {}", compId);

        Compilation compilation = getCompilationOrThrow(
                compilationRepository.findByIdWithEvents(compId), compId);

        Map<Long, Long> viewsMap = getViewsMap(compilation);
        Map<Long, Long> commentsMap = getCommentsMap(compilation);

        return CompilationMapper.toCompilationDto(compilation, viewsMap, commentsMap);
    }

    // методы для админа
    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        log.info("Добавляем подборку: {}", newCompilationDto);

        validationService.checkCompilationTitleUse(newCompilationDto.getTitle());

        Set<Long> newEvents = newCompilationDto.getEvents();
        Set<Event> events = new HashSet<>();
        if (newEvents != null && newEvents.isEmpty() == false) {
            events = new HashSet<>(
                    eventService.getAllEventById(newEvents));
        }

        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        compilation = compilationRepository.save(compilation);
        log.info("Добавлена подборка: {}", compilation);

        Map<Long, Long> viewsMap = getViewsMap(compilation);
        Map<Long, Long> commentsMap = getCommentsMap(compilation);

        return CompilationMapper.toCompilationDto(compilation, viewsMap, commentsMap);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.info("Удаление подборки id: {}", compId);
        validationService.checkCompilationExists(compId);
        compilationRepository.deleteById(compId);
        log.info("Удалена подборка id: {}", compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest) {
        log.info("Обновляем подборку id {}: {}", compId, updateRequest);

        Compilation compilation = getCompilationOrThrow(
                compilationRepository.findById(compId), compId);

        if (updateRequest.hasTitle()) {
            validationService.checkCompilationTitleUse(updateRequest.getTitle());
            compilation.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.hasPinned()) {
            compilation.setPinned(updateRequest.getPinned());
        }

        if (updateRequest.hasEvents()) {
            Set<Event> events = new HashSet<>(
                    eventService.getAllEventById(updateRequest.getEvents()));
            compilation.setEvents(events);
        }

        compilation = compilationRepository.save(compilation);
        log.info("Обновлена подборка: {}", compilation);

        Map<Long, Long> viewsMap = getViewsMap(compilation);
        Map<Long, Long> commentsMap = getCommentsMap(compilation);

        return CompilationMapper.toCompilationDto(compilation, viewsMap, commentsMap);
    }

    private Map<Long, Long> getViewsMap(Compilation compilation) {
        List<Event> compilationEvents = compilation.getEvents().stream().toList();
        return eventService.getViewStatsForEvents(compilationEvents);
    }

    private Map<Long, Long> getCommentsMap(Compilation compilation) {
        List<Long> eventIds = compilation.getEvents().stream().map(Event::getId).toList();
        return commentService.getCountNumberOfCommentsForEvent(eventIds);
    }

    private Compilation getCompilationOrThrow(Optional<Compilation> compOpt, Long compId) {
        log.info("Получаем сущность подборки id {}", compId);
        return compOpt
                .orElseThrow(() -> new NotFoundException(
                        String.format(NotFound.COMPILATION, compId)));
    }
}