package ru.practicum.ewm.stats.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class StatsClient {
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final RestTemplate restTemplate;
    private final String statsServiceUrl;

    public StatsClient(RestTemplate restTemplate,
                       @Value("${stats.service.url:http://localhost:9090}") String statsServiceUrl) {
        this.restTemplate = restTemplate;
        this.statsServiceUrl = statsServiceUrl;
    }

    public void sendHit(String app, String uri, String ip) {
        log.info("Готовим отправку в сервис статистики факта обращения к эндпоинту: app {}, uri {}, ip {}", app, uri, ip);
        EndpointHit hitDto = EndpointHit.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EndpointHit> request = new HttpEntity<>(hitDto, headers);

        try {
            restTemplate.exchange(
                    statsServiceUrl + "/hit",
                    HttpMethod.POST,
                    request,
                    Void.class
            );
        } catch (Exception e) {
            log.error("Не удалось отправить POST запрос на эндпоинт /hit службы статистики: {}", e.getMessage());
        }
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end,
                                    List<String> uris, boolean unique, Integer page, Integer size) {
        log.info("Готовим отправку запроса статистики обращений к эндпоинтам {} за период {} - {}", uris, start, end);
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates must not be null");
        }

        String startParam = start.format(formatter);
        String endParam = end.format(formatter);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(statsServiceUrl + "/stats")
                .queryParam("start", startParam)
                .queryParam("end", endParam)
                .queryParam("unique", unique)
                .queryParam("page", page)
                .queryParam("size", size);

        if (uris != null && uris.isEmpty() == false) {
            uriBuilder.queryParam("uris", uris.toArray());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            log.info("uriBuilder.toUriString() - {}\n request - {}", uriBuilder.toUriString(), request);
            ResponseEntity<List<ViewStats>> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<List<ViewStats>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            log.error("Не удалось отправить GET запрос на эндпоинт /stats службы статистики: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}