package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.server.mapper.EndpointHitMapper;
import ru.practicum.ewm.stats.server.model.EndpointHitEntity;
import ru.practicum.ewm.stats.server.model.ViewStatsProjection;
import ru.practicum.ewm.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;

    public void saveHit(EndpointHit hitDto) {
        log.info("Сохраняем обращение к эндпоинту: {}", hitDto);
        EndpointHitEntity hitEntity = EndpointHitMapper.toEntity(hitDto);
        statsRepository.save(hitEntity);
        log.info("Сохранено в БД обращение к эндпоинту: {}", hitDto);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end,
                                    List<String> uris, boolean unique, int page, int size) {
        log.info("Получаем статистику обращений к эндпоинтам {} за период {} - {}", uris, start, end);

        List<ViewStatsProjection> projections;

        if (uris == null || uris.isEmpty()) {
            Pageable pageable = PageRequest.of(page, size);
            projections = unique
                    ? statsRepository.getAllUniqueStats(start, end, pageable).getContent()
                    : statsRepository.getAllStats(start, end, pageable).getContent();
        } else {
            projections = unique
                    ? statsRepository.getUniqueStats(start, end, uris)
                    : statsRepository.getStats(start, end, uris);
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
}

