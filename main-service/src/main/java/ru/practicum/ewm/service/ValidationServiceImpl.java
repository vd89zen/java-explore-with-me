package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.request.CommentStatusUpdateRequest;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.enums.CommentStatus;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.model.utils.NotFound;
import ru.practicum.ewm.model.enums.RequestStatus;
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.service.api.ValidationService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationServiceImpl implements ValidationService {
    private static final int MIN_HOURS_BEFORE_EVENT = 2;
    private static final int MIN_HOURS_BEFORE_PUBLISH = 1;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;
    private final CompilationRepository compilationRepository;

    // валидация событий
    @Override
    public void checkEventExists(Long eventId) {
        log.info("Проверяем существует ли событие id {}", eventId);
        if (eventRepository.existsById(eventId) == false) {
            throw new NotFoundException(
                    String.format(NotFound.EVENT, eventId));
        }
    }

    @Override
    public void validateEventDateForCreateOrUpdate(LocalDateTime eventDate) {
        LocalDateTime now = LocalDateTime.now();
        log.info("Проверяем, что дата начала события {} не ранее чем через 2 часа от сейчас {}",
                eventDate, now);
        LocalDateTime minEventDate = now.plusHours(MIN_HOURS_BEFORE_EVENT);

        if (eventDate.isBefore(minEventDate)) {
            throw new ConflictException(String.format(
                    "Field: eventDate. Error: дата начала события должна быть не ранее чем через " +
                            "%dч. от текущего времени %s. Value: %s",
                    MIN_HOURS_BEFORE_EVENT, now, eventDate));
        }
    }

    @Override
    public void validateEventDateForPublish(LocalDateTime eventDate, LocalDateTime publishedOn) {
        log.info("Проверяем, что дата начала изменяемого события {} не ранее чем через час от даты публикации {}",
                eventDate, publishedOn);

        LocalDateTime minEventDate = publishedOn.plusHours(MIN_HOURS_BEFORE_PUBLISH);

        if (eventDate.isBefore(minEventDate)) {
            throw new ConflictException(String.format(
                    "Field: eventDate. Error: дата начала изменяемого события должна быть не ранее чем через " +
                            "%dч. от даты публикации %s. Value: %s",
                    MIN_HOURS_BEFORE_PUBLISH, publishedOn, eventDate));
        }
    }

    @Override
    public void validateEventCanBeUpdateByUser(EventState eventState) {
        log.info("Проверяем, что событие ещё не опубликовано(статус:{}), т.е. можно редактировать", eventState);
        if (eventState == EventState.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
    }

    @Override
    public void validateEventCanBePublishByAdmin(EventState eventState) {
        log.info("(админ)Проверяем можно ли опубликовать событие(статус:{})", eventState);
        if (eventState != EventState.PENDING) {
            throw new ConflictException(
                    String.format("Cannot publish the event because it's not in the right state: %s", eventState));
        }
    }

    @Override
    public void validateEventCanBeRejectByAdmin(EventState eventState) {
        log.info("(админ)Проверяем можно ли отклонить публикацию события(статус:{})", eventState);
        if (eventState == EventState.PUBLISHED) {
            throw new ConflictException("Cannot reject the event because it's already published");
        }
    }

    @Override
    public void validateEventExistsAndInitiator(Long eventId, Long userId) {
        if (eventRepository.existsByIdAndInitiatorId(eventId, userId) == false) {
            throw new NotFoundException(
                    String.format(NotFound.EVENT + " or user is not the initiator that event", eventId));
        }
    }

    // валидация пользователя
    @Override
    public void checkUserExists(Long userId) {
        log.info("Проверяем существует ли пользователь id {}", userId);
        if (userRepository.existsById(userId) == false) {
            throw new NotFoundException(
                    String.format(NotFound.USER, userId));
        }
    }

    @Override
    public void checkUserEmailUse(String email) {
        log.info("Проверяем занят ли email {}.", email);
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("User with Email " + email + "already exists");
        }
        log.info("email {} свободен.", email);
    }

    // валидация категории
    @Override
    public void validateCategoryDeletion(Long catId) {
        log.info("Проверяем условия для удаления категории {}", catId);
        checkCategoryExists(catId);
        if (eventRepository.existsEventsByCategoryId(catId)) {
            throw new ConflictException("The category is not empty");
        }
    }

    @Override
    public void checkCategoryExists(Long catId) {
        log.info("Проверяем наличие категории {}", catId);
        if (categoryRepository.existsById(catId) == false) {
            throw new NotFoundException(
                    String.format(NotFound.CATEGORY, catId));
        }
    }

    @Override
    public void checkCategoryNameUse(String name, Long catId) {
        log.info("Проверяем занято ли имя категории {}.", name);

        boolean exists = (catId == null)
                ? categoryRepository.existsByName(name)
                : categoryRepository.existsByNameAndIdNot(name, catId);

        if (exists) {
            throw new ConflictException("Category with name " + name + " already exists");
        }

        log.info("имя {} свободно.", name);
    }

    // валидация заявки
    @Override
    public void validateParticipationRequest(Long userId, Event event) {
        log.info("Проверяем условия для подачи заявки на участие пользователя id {} в событии id {}", userId, event);

        checkUserExists(userId);

        if (requestRepository.existsByEventIdAndRequesterId(event.getId(), userId)) {
            throw new ConflictException("Request already exists");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot add request to own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cannot participate in unpublished event");
        }

        if (event.getParticipantLimit() > 0) {
            long confirmedCount = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ConflictException("The participant limit has been reached");
            }
        }
    }

    @Override
    public void validateRequestCanBeReject(RequestStatus requestStatus) {
        if (requestStatus == RequestStatus.REJECTED) {
            throw new ConflictException("Cannot cancel rejected request");
        }

        if (requestStatus == RequestStatus.CANCELED) {
            throw new ConflictException("Request is already canceled");
        }
    }

    //валидация подборки
    @Override
    public void checkCompilationExists(Long compId) {
        log.info("Проверяем наличие подборки id {}", compId);
        if (compilationRepository.existsById(compId) == false) {
            throw new NotFoundException(
                    String.format(NotFound.COMPILATION, compId));
        }
    }

    @Override
    public void checkCompilationTitleUse(String title) {
        log.info("Проверяем занято ли название подборки {}.", title);
        if (compilationRepository.existsByTitle(title)) {
            throw new ConflictException("Compilation with title " + title + " already exists");
        }
        log.info("название {} свободно.", title);
    }

    // валидация дат при запросе поиска
    @Override
    public void validateDateForSearch(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        log.info("Проверяем, диапазон дат для поиска: start {} - end {}",
                rangeStart, rangeEnd);

        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException(String.format(
                    "Error: rangeEnd %s не может быть меньше rangeStart %s",
                    rangeEnd, rangeStart));
        }
    }

    // валидация комментария
    @Override
    public void checkReasonDeleteIsGiven(CommentStatusUpdateRequest statusUpdateRequest) {
        log.info("Проверяем указание причины удаления если новый статус комментария 'deleted'", statusUpdateRequest);

        CommentStatus newStatus = checkAndGetCommentStatus(statusUpdateRequest.getStatus());

        if (newStatus.equals(CommentStatus.DELETED) || newStatus.equals(CommentStatus.CENSURED)) {
            String reason = statusUpdateRequest.getReasonDelete();
            if (reason == null || reason.isBlank()) {
                throw new BadRequestException("Не указана причина удаления/цензуры комментария.");
            }
        }
    }

    @Override
    public void checkCommentWasCreatedByCommenter(List<Comment> comments, Long userId) {
        log.info("Проверяем принадлежность комментариев {} пользователю id {}", comments, userId);

        List<Long> wrongCommentIds = comments.stream()
                .filter(c -> c.getCommenter().getId().equals(userId) == false)
                .map(Comment::getId)
                .collect(Collectors.toList());

        if (wrongCommentIds.isEmpty() == false) {
            throw new ConflictException("Comments - " + wrongCommentIds + " - not belong to user id " + userId);
        }
    }

    @Override
    public void checkCommentsBelongEvent(List<Comment> comments, Long eventId) {
        log.info("Проверяем принадлежность комментариев {} событию id {}", comments, eventId);

        List<Long> wrongCommentIds = comments.stream()
                .filter(c -> c.getEvent().getId().equals(eventId) == false)
                .map(Comment::getId)
                .collect(Collectors.toList());

        if (wrongCommentIds.isEmpty() == false) {
            throw new ConflictException("Comments - " + wrongCommentIds + " - not belong to event id " + eventId);
        }
    }

    @Override
    public CommentStatus checkAndGetCommentStatus(String status) {
        log.info("Проверяем статус комментария на валидность: {}", status);
        return CommentStatus.from(status)
                .orElseThrow(() -> new BadRequestException("Unknown comment status: " + status));
    }

    @Override
    public void checkCommentStatusAccessibleForEventOwner(CommentStatus status) {
        log.info("Проверяем доступность комментария по статусу для владельца события: {}", status);
        if (getCommentStatusExcludedForEventOwner().contains(status)) {
            new ForbiddenException("You do not have permission to get comment with this status: " + status);
        };
    }

    @Override
    public Set<CommentStatus> checkAndGetCommentStatusForEventOwner(String status) {
        log.info("Проверяем статус комментария для показа создателю комментированного события: {}", status);

        if (status == null) {
            return getCommentStatusEventOwnerAccessible();
        } else {
            CommentStatus commentStatus = checkAndGetCommentStatus(status);
            checkCommentStatusAccessibleForEventOwner(commentStatus);
            return Set.of(commentStatus);
        }
    }

    @Override
    public void checkCommentStatusChangeCommenter(CommentStatus newStatus, CommentStatus oldStatus) {
        log.info("Проверяем допустимость смены статуса комментария создателем комментария: new {} - old {}",
                newStatus, oldStatus);

        if (newStatus.equals(CommentStatus.DELETED) && oldStatus.equals(CommentStatus.POSTED)) {
            return;
        }

        throw new ConflictException(String.format("Commentator can't change comment status %s to %s.",
                oldStatus.name(), newStatus));
    }

    @Override
    public void checkCommentStatusChangeEventOwner(CommentStatus newStatus, CommentStatus oldStatus) {
        log.info("Проверяем допустимость смены статуса комментария создателем события: new {} - old {}",
                newStatus, oldStatus);

        if (newStatus.equals(CommentStatus.POSTED) || newStatus.equals(CommentStatus.REJECTED)) {
            if (oldStatus.equals(CommentStatus.PENDING) || oldStatus.equals(CommentStatus.UPDATED)) {
                return;
            }
        }

        if (newStatus.equals(CommentStatus.DELETED) && oldStatus.equals(CommentStatus.POSTED)) {
            return;
        }

        throw new ConflictException(String.format("Event owner can't change comment status %s to %s.",
                oldStatus.name(), newStatus));
    }

    @Override
    public void checkCommentStatusChangeAdmin(CommentStatus newStatus, CommentStatus oldStatus) {
        log.info("Проверяем допустимость смены статуса комментария админом: new {} - old {}",
                newStatus, oldStatus);

        if (newStatus.equals(CommentStatus.PENDING) || newStatus.equals(CommentStatus.POSTED)) {
            if (oldStatus.equals(CommentStatus.REJECTED) || oldStatus.equals(CommentStatus.DELETED) ||
                    oldStatus.equals(CommentStatus.CENSURED)) {
                return;
            }
        }

        if (newStatus.equals(CommentStatus.DELETED) || newStatus.equals(CommentStatus.CENSURED)) {
            if (oldStatus.equals(CommentStatus.POSTED)) {
                return;
            }
        }

        throw new ConflictException(String.format("Admin can't change comment status %s to %s.",
                oldStatus.name(), newStatus));
    }

    /**
     * Возвращает набор статусов комментария, при которых комментарий доступен для владельца события.
     */
    @Override
    public Set<CommentStatus> getCommentStatusEventOwnerAccessible() {
        Set<CommentStatus> excluded = getCommentStatusExcludedForEventOwner();
        return EnumSet.copyOf(
                Arrays.stream(CommentStatus.values())
                        .filter(cs -> excluded.contains(cs) == false)
                        .collect(Collectors.toCollection(() -> EnumSet.noneOf(CommentStatus.class)))
        );
    }

    /**
     * Возвращает набор статусов комментариев, которые исключаются (не должны учитываться)
     * при фильтрации для владельца события.
     */
    private Set<CommentStatus> getCommentStatusExcludedForEventOwner() {
        return EnumSet.of(CommentStatus.REJECTED, CommentStatus.DELETED, CommentStatus.CENSURED);
    }
}