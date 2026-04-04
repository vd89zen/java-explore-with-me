package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.CommentStatusUpdateRequest;
import ru.practicum.ewm.dto.response.CommentFullAdminDto;
import ru.practicum.ewm.service.api.CommentService;

import java.util.List;
import java.util.Set;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class CommentAdminController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentFullAdminDto>> getComments(
            @RequestParam(required = false) List<Long> commentsIds,
            @RequestParam(name = "statuses", required = false) Set<String> statuses,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {

        return ResponseEntity.ok(
                commentService.getCommentsForAdmin(commentsIds, statuses, from, size));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentFullAdminDto> getCommentById(
            @PathVariable long commentId) {

        return ResponseEntity.ok(
                commentService.getCommentByIdForAdmin(commentId));
    }

    @PatchMapping
    public ResponseEntity<List<CommentFullAdminDto>> changeCommentStatus(
            @Valid @RequestBody CommentStatusUpdateRequest statusUpdateRequest) {
        return ResponseEntity.ok(
                commentService.changeCommentStatusByAdmin(statusUpdateRequest));
    }
}