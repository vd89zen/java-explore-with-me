package ru.practicum.ewm.stats.server.service;

import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.util.List;

public interface StatsService {

    void saveHit(EndpointHit hitDto);

    List<ViewStats> getStats(String start, String end,
                             List<String> uris, boolean unique, int page, int size);
}
