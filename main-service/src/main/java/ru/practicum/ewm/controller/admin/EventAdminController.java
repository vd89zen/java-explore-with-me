package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.response.CommentFullAdminDto;
import ru.practicum.ewm.dto.response.EventFullDto;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.service.api.CommentService;
import ru.practicum.ewm.service.api.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventAdminController {

    private final EventService eventService;
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {

        return ResponseEntity.ok(
                eventService.getEventsAdmin(
                        users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable long eventId,
                                                    @Valid @RequestBody UpdateEventAdminRequest updateRequest) {
        return ResponseEntity.ok(
                eventService.updateEventByIdAdmin(eventId, updateRequest));
    }

    @GetMapping("/{eventId}/comments")
    public ResponseEntity<List<CommentFullAdminDto>> getEventComments(
            @PathVariable(name = "eventId") long eventId,
            @RequestParam(name = "status", required = false) Set<String> statuses,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {

        return ResponseEntity.ok(
                commentService.getEventCommentsForAdmin(eventId, statuses, from, size));
    }
}
