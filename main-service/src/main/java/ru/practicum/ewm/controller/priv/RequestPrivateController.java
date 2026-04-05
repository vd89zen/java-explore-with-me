package ru.practicum.ewm.controller.priv;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.response.ParticipationRequestDto;
import ru.practicum.ewm.service.api.ParticipationRequestService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestPrivateController {

    private final ParticipationRequestService participationRequestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable long userId) {
        return ResponseEntity.ok(
                participationRequestService.getUserRequests(userId));
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> addParticipationRequest(@PathVariable long userId,
                                                                           @RequestParam @Positive Long eventId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(participationRequestService.addParticipationRequest(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable long userId,
                                                                 @PathVariable long requestId) {
        return ResponseEntity.ok(
                participationRequestService.cancelRequest(userId, requestId));
    }
}