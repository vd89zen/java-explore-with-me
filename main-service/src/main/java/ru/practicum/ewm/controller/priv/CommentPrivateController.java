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
import ru.practicum.ewm.dto.request.NewCommentDto;
import ru.practicum.ewm.dto.request.UpdateCommentRequest;
import ru.practicum.ewm.dto.response.CommentFullCommenterDto;
import ru.practicum.ewm.dto.response.CommentShortDto;
import ru.practicum.ewm.service.api.CommentService;

import java.util.List;
import java.util.Set;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class CommentPrivateController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentFullCommenterDto>> getComments(
            @PathVariable(name = "userId") long userId,
            @RequestParam(name = "statuses", required = false) Set<String> statuses,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {

        return ResponseEntity.ok(
                commentService.getCommentsForCommenter(userId, statuses, from, size));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentFullCommenterDto> getCommentById(
            @PathVariable(name = "userId") long userId, @PathVariable(name = "commentId") long commentId) {

        return ResponseEntity.ok(
                commentService.getCommentByIdForCommenter(userId, commentId));
    }

    @PostMapping
    public ResponseEntity<CommentShortDto> addComment(
            @PathVariable(name = "userId") long userId, @Valid @RequestBody NewCommentDto newComment) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.addComment(userId, newComment));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentFullCommenterDto> updateComment(
            @PathVariable(name = "userId") long userId, @PathVariable(name = "commentId") long commentId,
            @Valid @RequestBody UpdateCommentRequest updateCommentRequest) {

        return ResponseEntity.ok(
                commentService.updateComment(userId, commentId, updateCommentRequest));
    }

    @PatchMapping
    public ResponseEntity<List<CommentFullCommenterDto>> changeCommentStatus(
            @PathVariable(name = "userId") long userId,
            @Valid @RequestBody CommentStatusUpdateRequest statusUpdateRequest) {

        return ResponseEntity.ok(
                commentService.changeCommentStatusByCommenter(userId, statusUpdateRequest));
    }
}