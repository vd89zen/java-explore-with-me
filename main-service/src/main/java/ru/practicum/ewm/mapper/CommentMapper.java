package ru.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.request.NewCommentDto;
import ru.practicum.ewm.dto.response.CommentFullAdminDto;
import ru.practicum.ewm.dto.response.CommentFullCommenterDto;
import ru.practicum.ewm.dto.response.CommentFullEventOwnerDto;
import ru.practicum.ewm.dto.response.CommentShortDto;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.CommentStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentMapper {

    public static Comment toComment(NewCommentDto dto, Event event, User user) {
        return Comment.builder()
                .event(event)
                .commenter(user)
                .text(dto.getText())
                .createdOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .status(CommentStatus.PENDING)
                .build();
    }

    public static CommentFullCommenterDto toCommentFullCommenterDto(Comment comment) {
        return CommentFullCommenterDto.builder()
                .id(comment.getId())
                .event(EventMapper.toEventAnnotationDto(comment.getEvent()))
                .commenter(UserMapper.toUserShortDto(comment.getCommenter()))
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .updatedOn(comment.getUpdatedOn())
                .postedOn(comment.getPostedOn())
                .status(comment.getStatus().name())
                .reasonDelete(comment.getReasonDelete())
                .statusChangedByAdmin(comment.getStatusChangedByAdmin())
                .build();
    }

    public static CommentFullEventOwnerDto toCommentFullEventOwnerDto(Comment comment) {
        return CommentFullEventOwnerDto.builder()
                .id(comment.getId())
                .event(EventMapper.toEventAnnotationDto(comment.getEvent()))
                .commenter(UserMapper.toUserShortDto(comment.getCommenter()))
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .updatedOn(comment.getUpdatedOn())
                .postedOn(comment.getPostedOn())
                .status(comment.getStatus().name())
                .statusChangedByAdmin(comment.getStatusChangedByAdmin())
                .build();
    }

    public static CommentFullAdminDto toCommentFullAdminDto(Comment comment) {
        return CommentFullAdminDto.builder()
                .id(comment.getId())
                .event(EventMapper.toEventAnnotationDto(comment.getEvent()))
                .commenter(UserMapper.toUserShortDto(comment.getCommenter()))
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .updatedOn(comment.getUpdatedOn())
                .postedOn(comment.getPostedOn())
                .status(comment.getStatus().name())
                .reasonDelete(comment.getReasonDelete())
                .statusEditor(
                        comment.getStatusEditor() != null ? UserMapper.toUserShortDto(comment.getStatusEditor()) : null)
                .statusChangedByAdmin(comment.getStatusChangedByAdmin())
                .build();
    }

    public static CommentShortDto toCommentShortDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getId())
                .commenter(UserMapper.toUserShortDto(comment.getCommenter()))
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .status(comment.getStatus().name())
                .build();
    }
}
