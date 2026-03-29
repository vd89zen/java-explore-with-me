package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.server.exception.BadRequestException;
import ru.practicum.ewm.stats.server.mapper.EndpointHitMapper;
import ru.practicum.ewm.stats.server.model.EndpointHitEntity;
import ru.practicum.ewm.stats.server.model.ViewStatsProjection;
import ru.practicum.ewm.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public void saveHit(EndpointHit hitDto) {
        log.info("Сохраняем обращение к эндпоинту: {}", hitDto);
        EndpointHitEntity hitEntity = EndpointHitMapper.toEntity(hitDto);
        statsRepository.save(hitEntity);
        log.info("Сохранено в БД обращение к эндпоинту: {}", hitDto);
    }

    @Override
    public List<ViewStats> getStats(String start, String end,
                                    List<String> uris, boolean unique, int page, int size) {
        log.info("Получаем статистику обращений к эндпоинтам {} за период {} - {}", uris, start, end);

        LocalDateTime truncatedStart = parseToLocalDateTime(start).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime truncatedEnd = parseToLocalDateTime(end).truncatedTo(ChronoUnit.SECONDS);

        validateDateRange(truncatedStart, truncatedEnd);

        List<ViewStatsProjection> projections;

        if (uris == null || uris.isEmpty()) {
            Pageable pageable = PageRequest.of(page, size);
            projections = unique
                    ? statsRepository.getAllUniqueStats(truncatedStart, truncatedEnd, pageable).getContent()
                    : statsRepository.getAllStats(truncatedStart, truncatedEnd, pageable).getContent();
        } else {
            projections = unique
                    ? statsRepository.getUniqueStats(truncatedStart, truncatedEnd, uris)
                    : statsRepository.getStats(truncatedStart, truncatedEnd, uris);
        }

        List<ViewStats> stats = projections.stream()
                .map(this::toViewStats)
                .collect(Collectors.toList());
        log.info("Получена статистика обращений к эндпоинтам: {}", stats);
        return stats;
    }

    private ViewStats toViewStats(ViewStatsProjection projection) {
        return ViewStats.builder()
                .app(projection.getApp())
                .uri(projection.getUri())
                .hits(projection.getHits())
                .build();
    }

    private LocalDateTime parseToLocalDateTime(String date) {
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException("Invalid date format: " + date +
                        ". Expected format: yyyy-MM-ddTHH:mm:ss or yyyy-MM-dd HH:mm:ss");
            }
        }
    }

    private void validateDateRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        log.info("Проверяем, диапазон дат для поиска: start {} - end {}",
                rangeStart, rangeEnd);

        if (rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException(String.format(
                    "Error: rangeEnd %s не может быть меньше rangeStart %s",
                    rangeEnd, rangeStart));
        }
    }
}

