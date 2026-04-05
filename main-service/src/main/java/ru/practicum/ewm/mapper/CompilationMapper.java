package ru.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.request.NewCompilationDto;
import ru.practicum.ewm.dto.response.CompilationDto;
import ru.practicum.ewm.dto.response.EventShortDto;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationMapper {
    public static final Boolean PINNED_DEFAULT_VALUE = false;

    public static Compilation toCompilation(NewCompilationDto newCompilationDto, Set<Event> events) {
        return Compilation.builder()
                .pinned(newCompilationDto
                        .getPinned() != null ? newCompilationDto.getPinned() : PINNED_DEFAULT_VALUE)
                .title(newCompilationDto.getTitle())
                .events(events != null ? events : Set.of())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation,
                                           Map<Long, Long> viewsMap, Map<Long, Long> commentsMap) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream()
                        .map(event -> {
                            Long eventId = event.getId();
                            EventShortDto dto = EventMapper.toEventShortDto(
                                    event,
                                    viewsMap.getOrDefault(eventId, EventMapper.NO_VIEWS),
                                    commentsMap.getOrDefault(eventId, EventMapper.NO_COMMENTS));
                            return dto;
                        })
                        .collect(Collectors.toSet()))
                .build();
    }
}