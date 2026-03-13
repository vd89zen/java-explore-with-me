package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;

    public void saveHit(EndpointHit hitDto) {
        EndpointHitEntity hitEntity = EndpointHitMapper.toEntity(hitDto);
        statsRepository.save(hitEntity);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end,
                                    List<String> uris, boolean unique, int page, int size) {

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

        return projections.stream()
                .map(this::toViewStats)
                .collect(Collectors.toList());
    }

    private ViewStats toViewStats(ViewStatsProjection projection) {
        return ViewStats.builder()
                .app(projection.getApp())
                .uri(projection.getUri())
                .hits(projection.getHits())
                .build();
    }
}

