package ru.practicum.ewm.controller.pub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.response.EventFullDto;
import ru.practicum.ewm.dto.response.EventShortDto;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.model.enums.EventSort;
import ru.practicum.ewm.service.api.EventService;
import ru.practicum.ewm.stats.client.StatsClient;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPublicController {

    @Value("${spring.application.name:ewm-main-service}")
    private String appName;

    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(name = "sort", defaultValue = "event_date") String sortParam,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest request) {

        EventSort sort = EventSort.from(sortParam)
                .orElseThrow(() -> new BadRequestException("Unknown sort: " + sortParam));

        List<EventShortDto> events = eventService.getEventsPublic(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        statsClient.sendHit(appName, request.getRequestURI(), request.getRemoteAddr());

        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable long id, HttpServletRequest request) {
        EventFullDto event = eventService.getEventByIdPublic(id);
        statsClient.sendHit(appName, request.getRequestURI(), request.getRemoteAddr());
        return ResponseEntity.ok(event);
    }
}

