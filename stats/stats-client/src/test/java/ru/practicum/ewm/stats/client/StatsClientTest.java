package ru.practicum.ewm.stats.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsClientTest {

    private static final String BASE_URL = "http://localhost:9090";
    private static final LocalDateTime TEST_DATE_TIME =
            LocalDateTime.of(2026, 3, 13, 12, 0, 0);

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private StatsClient statsClient;

    @BeforeEach
    void setUp() {
        statsClient = new StatsClient(restTemplate, BASE_URL);
    }

    @Test
    @DisplayName("Отправка hit-запроса с корректными данными")
    void sendHit_Should_Send_Post_Request_With_Valid_Data_Test() {
        // Given
        String app = "test-app";
        String uri = "/test-uri";
        String ip = "192.168.0.1";

        when(restTemplate.exchange(
                eq(BASE_URL + "/hit"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(ResponseEntity.ok().build());

        // When
        statsClient.sendHit(app, uri, ip);

        // Then
        verify(restTemplate, times(1)).exchange(
                eq(BASE_URL + "/hit"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class)
        );

        ArgumentCaptor<HttpEntity<EndpointHit>> argument = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                anyString(),
                any(HttpMethod.class),
                argument.capture(),
                any(Class.class)
        );

        HttpEntity<EndpointHit> requestEntity = argument.getValue();
        assertNotNull(requestEntity);
        assertEquals(MediaType.APPLICATION_JSON, requestEntity.getHeaders().getContentType());

        EndpointHit endpointHit = requestEntity.getBody();
        assertNotNull(endpointHit);
        assertEquals(app, endpointHit.getApp());
        assertEquals(uri, endpointHit.getUri());
        assertEquals(ip, endpointHit.getIp());
        assertNotNull(endpointHit.getTimestamp());
    }

    @Test
    @DisplayName("Получение статистики с полными параметрами")
    void getStats_Should_Send_Get_Request_With_All_Parameters_Test() {
        // Given
        LocalDateTime start = TEST_DATE_TIME.minusDays(1);
        LocalDateTime end = TEST_DATE_TIME;
        List<String> uris = Arrays.asList("/uri1", "/uri2");
        boolean unique = true;
        Integer page = 0;
        Integer size = 10;

        List<ViewStats> expectedStats = Arrays.asList(
                new ViewStats("app1", "/uri1", 5L),
                new ViewStats("app2", "/uri2", 3L)
        );

        ResponseEntity<List<ViewStats>> responseEntity = ResponseEntity.ok(expectedStats);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // When
        List<ViewStats> result = statsClient.getStats(start, end, uris, unique, page, size);

        // Then
        assertNotNull(result);
        assertEquals(expectedStats, result);

        verify(restTemplate, times(1)).exchange(
                contains("/stats?start=2026-03-12%2012:00:00"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
        verify(restTemplate, times(1)).exchange(
                contains("&end=2026-03-13%2012:00:00"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
        verify(restTemplate, times(1)).exchange(
                contains("&unique=true"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
        verify(restTemplate, times(1)).exchange(
                contains("&page=0"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
        verify(restTemplate, times(1)).exchange(
                contains("&size=10"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
        verify(restTemplate, times(1)).exchange(
                contains("&uris=/uri1&uris=/uri2"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("Получение статистики без списка URIs")
    void getStats_Should_Work_With_Null_Uris_Test() {
        // Given
        LocalDateTime start = TEST_DATE_TIME.minusDays(1);
        LocalDateTime end = TEST_DATE_TIME;
        boolean unique = false;

        List<ViewStats> expectedStats = Collections.singletonList(
                new ViewStats("test-app", "/test", 1L)
        );

        ResponseEntity<List<ViewStats>> responseEntity = ResponseEntity.ok(expectedStats);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // When
        List<ViewStats> result = statsClient.getStats(start, end, null, unique, null, null);

        // Then
        assertNotNull(result);
        assertEquals(expectedStats, result);

        verify(restTemplate, times(1)).exchange(
                contains("/stats?start=2026-03-12%2012:00:00"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
        verify(restTemplate, times(1)).exchange(
                contains("&end=2026-03-13%2012:00:00"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
        verify(restTemplate, times(1)).exchange(
                contains("&unique=false"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        );
    }
}
