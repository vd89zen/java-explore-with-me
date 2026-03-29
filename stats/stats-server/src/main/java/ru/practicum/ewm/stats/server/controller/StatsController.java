package ru.practicum.ewm.stats.server.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.server.service.StatsService;
import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping(path = "/hit")
    public ResponseEntity<Void> saveHit(@RequestBody @Valid EndpointHit hitDto) {
        statsService.saveHit(hitDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping(path = "/stats")
    public ResponseEntity<List<ViewStats>> getStats(
            @RequestParam @NotBlank String start,
            @RequestParam @NotBlank String end,
            @RequestParam (required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique,
            @PositiveOrZero @RequestParam(name = "page", defaultValue = "0") int page,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        List<ViewStats> stats = statsService.getStats(start, end, uris, unique, page, size);
        return ResponseEntity.ok(stats);
    }
}
