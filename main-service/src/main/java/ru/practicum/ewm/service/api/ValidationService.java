package ru.practicum.ewm.service.api;

import ru.practicum.ewm.dto.request.CommentStatusUpdateRequest;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.enums.CommentStatus;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.model.enums.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ValidationService {

    // валидация события
    void checkEventExists(Long eventId);

    void validateEventDateForCreateOrUpdate(LocalDateTime eventDate);

    void validateEventDateForPublish(LocalDateTime eventDate, LocalDateTime publishedOn);

    void validateEventCanBeUpdateByUser(EventState eventState);

    void validateEventCanBePublishByAdmin(EventState eventState);

    void validateEventCanBeRejectByAdmin(EventState eventState);

    void validateEventExistsAndInitiator(Long eventId, Long userId);

    // валидация пользователя
    void checkUserExists(Long userId);

    void checkUserEmailUse(String email);

    // валидация категории
    void validateCategoryDeletion(Long catId);

    void checkCategoryExists(Long catId);

    void checkCategoryNameUse(String name, Long catId);

    // валидация заявки
    void validateParticipationRequest(Long userId, Event event);

    void validateRequestCanBeReject(RequestStatus requestStatus);

    // валидация подборки
    void checkCompilationExists(Long compId);

    void checkCompilationTitleUse(String title);

    // валидация дат при запросе
    void validateDateForSearch(LocalDateTime rangeStart, LocalDateTime rangeEnd);

    // валидация комментария
    void checkReasonDeleteIsGiven(CommentStatusUpdateRequest statusUpdateRequest);

    void checkCommentWasCreatedByCommenter(List<Comment> comments, Long userId);

    void checkCommentsBelongEvent(List<Comment> comments, Long eventId);

    CommentStatus checkAndGetCommentStatus(String status);

    void checkCommentStatusAccessibleForEventOwner(CommentStatus status);

    Set<CommentStatus> checkAndGetCommentStatusForEventOwner(String status);

    void checkCommentStatusChangeCommenter(CommentStatus newStatus, CommentStatus oldStatus);

    void checkCommentStatusChangeEventOwner(CommentStatus newStatus, CommentStatus oldStatus);

    void checkCommentStatusChangeAdmin(CommentStatus newStatus, CommentStatus oldStatus);

    Set<CommentStatus> getCommentStatusEventOwnerAccessible();

}
