package ru.practicum.ewm.controller.priv;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.CommentStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.response.*;
import ru.practicum.ewm.dto.request.NewEventDto;
import ru.practicum.ewm.dto.request.UpdateEventUserRequest;
import ru.practicum.ewm.service.api.CommentService;
import ru.practicum.ewm.service.api.EventService;
import ru.practicum.ewm.service.api.ParticipationRequestService;

import java.util.List;
import java.util.Set;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;
    private final ParticipationRequestService participationRequestService;
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(@PathVariable long userId,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                         @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(
                eventService.getEventsPrivate(userId, from, size));
    }

    @PostMapping
    public ResponseEntity<EventFullDto> addEvent(@PathVariable long userId,
                                                 @Valid @RequestBody NewEventDto newEventDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.addEventPrivate(userId, newEventDto));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable long userId, @PathVariable long eventId) {
        return ResponseEntity.ok(
                eventService.getEventByIdPrivate(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable long userId, @PathVariable long eventId,
                                                    @Valid @RequestBody UpdateEventUserRequest updateRequest) {
        return ResponseEntity.ok(
                eventService.updateEventByIdPrivate(userId, eventId, updateRequest));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(@PathVariable long userId,
                                                                              @PathVariable long eventId) {
        return ResponseEntity.ok(
                participationRequestService.getEventRequests(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(
            @PathVariable long userId,
            @PathVariable long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) {
        return ResponseEntity.ok(
                participationRequestService.changeRequestStatus(userId, eventId, statusUpdateRequest));
    }

    @GetMapping("/{eventId}/comments")
    public ResponseEntity<List<CommentFullEventOwnerDto>> getEventComments(
            @PathVariable(name = "userId") long userId, @PathVariable long eventId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {

        return ResponseEntity.ok(
                commentService.getEventCommentsForEventOwner(userId, eventId, status, from, size));
    }

    @GetMapping("/{eventId}/comments/{commentId}")
    public ResponseEntity<CommentFullEventOwnerDto> getEventCommentById(
            @PathVariable long userId, @PathVariable long eventId, @PathVariable long commentId) {

        return ResponseEntity.ok(
                commentService.getEventCommentByIdForEventOwner(userId, eventId, commentId));
    }

    @PatchMapping("/{eventId}/comments")
    public ResponseEntity<List<CommentFullEventOwnerDto>> changeCommentStatus(
            @PathVariable(name = "userId") long userId,
            @PathVariable(name = "eventId") long eventId,
            @Valid @RequestBody CommentStatusUpdateRequest statusUpdateRequest) {
        return ResponseEntity.ok(
                commentService.changeCommentStatusByEventOwner(userId, eventId, statusUpdateRequest));
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentFullEventOwnerDto>> getAllEventComments(
            @PathVariable(name = "userId") long userId,
            @RequestParam(name = "status", required = false) Set<String> statuses,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {

        return ResponseEntity.ok(
                commentService.getAllEventCommentsForEventOwner(userId, statuses, from, size));
    }
}