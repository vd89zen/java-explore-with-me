package ru.practicum.ewm.stats.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.server.model.EndpointHitEntity;
import ru.practicum.ewm.stats.server.model.ViewStatsProjection;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHitEntity, Long> {

    @Query(value = "SELECT e.app as app, e.uri as uri, COUNT(e.id) as hits " +
            "FROM EndpointHitEntity e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR e.uri IN :uris) " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY hits DESC")
    List<ViewStatsProjection> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT e.app as app, e.uri as uri, COUNT(DISTINCT e.ip) as hits " +
            "FROM EndpointHitEntity e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR e.uri IN :uris) " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY hits DESC")
    List<ViewStatsProjection> getUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT e.app as app, e.uri as uri, COUNT(e.id) as hits " +
            "FROM EndpointHitEntity e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY hits DESC")
    Page<ViewStatsProjection> getAllStats(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT e.app as app, e.uri as uri, COUNT(DISTINCT e.ip) as hits " +
            "FROM EndpointHitEntity e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY hits DESC")
    Page<ViewStatsProjection> getAllUniqueStats(LocalDateTime start, LocalDateTime end, Pageable pageable);
}

