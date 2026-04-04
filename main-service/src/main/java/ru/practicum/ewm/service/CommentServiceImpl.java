package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.common.CommentsCountProjection;
import ru.practicum.ewm.dto.request.CommentStatusUpdateRequest;
import ru.practicum.ewm.dto.request.NewCommentDto;
import ru.practicum.ewm.dto.request.UpdateCommentRequest;
import ru.practicum.ewm.dto.response.CommentFullAdminDto;
import ru.practicum.ewm.dto.response.CommentFullCommenterDto;
import ru.practicum.ewm.dto.response.CommentFullEventOwnerDto;
import ru.practicum.ewm.dto.response.CommentShortDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.CommentStatus;
import ru.practicum.ewm.model.utils.NotFound;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.api.CommentService;
import ru.practicum.ewm.service.api.UserService;
import ru.practicum.ewm.service.api.ValidationService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final UserService userService;
    private final EventRepository eventRepository;
    private final ValidationService validationService;
    private final CommentRepository commentRepository;


    // commenter
    @Override
    public CommentFullCommenterDto getCommentByIdForCommenter(Long userId, Long commentId) {
        log.info("Получаем комментарий id {} пользователя id {}", commentId, userId);

        validationService.checkUserExists(userId);

        Comment comment = getCommentOrThrow(
                commentRepository.findByIdAndCommenterId(commentId, userId), commentId);

        log.info("Для пользователя id {} получили комментарий: {}", userId, comment);
        return CommentMapper.toCommentFullCommenterDto(comment);
    }

    @Override
    public List<CommentFullCommenterDto> getCommentsForCommenter(Long userId, Set<String> statuses, int from, int size) {
        log.info("Получаем комментарии пользователя id {}, statuses {}, from {} size {}",
                userId, statuses, from, size);

        validationService.checkUserExists(userId);

        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        List<Comment> comments;
        if (statuses == null || statuses.isEmpty()) {
            comments = commentRepository.findByCommenterId(userId, pageable).getContent();
        } else {
            Set<CommentStatus> commentStatuses = statuses.stream()
                    .map(s -> validationService.checkAndGetCommentStatus(s))
                    .collect(Collectors.toSet());
            comments = commentRepository.findByCommenterIdAndStatusIn(userId, commentStatuses, pageable).getContent();
        }

        log.info("Для пользователя id {} получили его комментарии: {}", userId, comments);
        return comments.stream()
                .map(CommentMapper::toCommentFullCommenterDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentShortDto addComment(Long userId, NewCommentDto newCommentDto) {
        log.info("Добавляем новый комментарий {}", userId, newCommentDto);
        User user = userService.getUserById(userId);
        Event event = eventRepository.findById(newCommentDto.getEventId())
                .orElseThrow(() -> new NotFoundException(String.format(NotFound.EVENT, newCommentDto.getEventId())));
        Comment comment = CommentMapper.toComment(newCommentDto, event, user);
        commentRepository.save(comment);
        log.info("Добавили комментарий {}", comment);
        return CommentMapper.toCommentShortDto(comment);
    }

    @Override
    @Transactional
    public CommentFullCommenterDto updateComment(
            Long userId, Long commentId, UpdateCommentRequest updateCommentRequest) {
        log.info("Пользователь id {} обновляет комментарий id {}: {}", userId, commentId, updateCommentRequest);

        User user = userService.getUserById(userId);

        Comment comment = getCommentOrThrow(
                commentRepository.findByIdAndCommenterIdAndStatus(commentId, userId, CommentStatus.POSTED), commentId);

        comment.setText(updateCommentRequest.getText());
        comment.setUpdatedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        comment.setStatus(CommentStatus.UPDATED);
        comment.setStatusEditor(user);

        comment = commentRepository.save(comment);

        log.info("Пользователь id {} обновил комментарий: {}", userId, comment);
        return CommentMapper.toCommentFullCommenterDto(comment);
    }

    @Override
    @Transactional
    public List<CommentFullCommenterDto> changeCommentStatusByCommenter(
            Long userId, CommentStatusUpdateRequest statusUpdateRequest) {
        log.info("У комментариев их создатель id {} меняет статус {}",
                userId, statusUpdateRequest);

        validationService.checkReasonDeleteIsGiven(statusUpdateRequest);

        User user = userService.getUserById(userId);

        CommentStatus newStatus = validationService.checkAndGetCommentStatus(statusUpdateRequest.getStatus());

        List<Comment> comments = commentRepository.findAllByIdWithLock(
                statusUpdateRequest.getCommentIds());

        validationService.checkCommentWasCreatedByCommenter(comments, userId);

        comments.forEach(c -> {
            validationService.checkCommentStatusChangeCommenter(newStatus, c.getStatus());
            c.setStatus(newStatus);
            c.setStatusEditor(user);
            c.setReasonDelete(statusUpdateRequest.getReasonDelete());
        });

        commentRepository.saveAll(comments);

        log.info("У комментариев их создатель id {} поменял статус {}", userId, comments);
        return comments.stream()
                .map(CommentMapper::toCommentFullCommenterDto)
                .collect(Collectors.toList());
    }

    // admin
    @Override
    public CommentFullAdminDto getCommentByIdForAdmin(Long commentId) {
        log.info("Получаем для админа комментарий id {}", commentId);

        Comment comment = getCommentOrThrow(
                commentRepository.findById(commentId), commentId);

        log.info("Получили для админа комментарий {}", comment);
        return CommentMapper.toCommentFullAdminDto(comment);
    }

    @Override
    public List<CommentFullAdminDto> getCommentsForAdmin(List<Long> commentIds, Set<String> statuses,
                                                         int from, int size) {
        log.info("Получаем комментарии {} (null=all) для админа, statuses {}, from {} size {}",
                commentIds, statuses, from, size);

        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        List<Comment> comments;
        if (commentIds == null || commentIds.isEmpty()) {
            if (statuses == null || statuses.isEmpty()) {
                comments = commentRepository.findAll(pageable).getContent();
            } else {
                Set<CommentStatus> commentStatuses = statuses.stream()
                        .map(s -> validationService.checkAndGetCommentStatus(s))
                        .collect(Collectors.toSet());
                comments = commentRepository.findByStatusIn(commentStatuses, pageable).getContent();
            }
        } else {
            if (statuses == null || statuses.isEmpty()) {
                comments = commentRepository.findByIdIn(commentIds, pageable).getContent();
            } else {
                Set<CommentStatus> commentStatuses = statuses.stream()
                        .map(s -> validationService.checkAndGetCommentStatus(s))
                        .collect(Collectors.toSet());
                comments = commentRepository.findByIdInAndStatusIn(commentIds, commentStatuses, pageable).getContent();
            }
        }

        log.info("Получили комментарии для админа: {}", comments);
        return comments.stream()
                .map(CommentMapper::toCommentFullAdminDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentFullAdminDto> getEventCommentsForAdmin(Long eventId, Set<String> statuses,
                                                              int from, int size) {
        log.info("Получаем комментарии для события id {} для админа, statuses {}, from {}, size {}",
                eventId, statuses, from, size);

        validationService.checkEventExists(eventId);

        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        List<Comment> comments;
        if (statuses == null || statuses.isEmpty()) {
            comments = commentRepository.findByEventId(eventId, pageable).getContent();
        } else {
            Set<CommentStatus> commentStatuses = statuses.stream()
                    .map(s -> validationService.checkAndGetCommentStatus(s))
                    .collect(Collectors.toSet());
            comments = commentRepository.findByEventIdAndStatusIn(eventId, commentStatuses, pageable).getContent();
        }

        log.info("Получили комментарии для события id {} для админа: {}", eventId, comments);
        return comments.stream()
                .map(CommentMapper::toCommentFullAdminDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CommentFullAdminDto> changeCommentStatusByAdmin(CommentStatusUpdateRequest statusUpdateRequest) {
        log.info("Админ меняет статус комментариев {}", statusUpdateRequest);

        validationService.checkReasonDeleteIsGiven(statusUpdateRequest);

        CommentStatus newStatus = validationService.checkAndGetCommentStatus(statusUpdateRequest.getStatus());

        List<Comment> comments = commentRepository.findAllByIdWithLock(
                statusUpdateRequest.getCommentIds());

        if (newStatus.equals(CommentStatus.POSTED)) {
            comments.forEach(c -> {
                validationService.checkCommentStatusChangeAdmin(newStatus, c.getStatus());
                c.setStatus(newStatus);
                c.setPostedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                c.setReasonDelete(statusUpdateRequest.getReasonDelete());
                c.setStatusEditor(null);
                c.setStatusChangedByAdmin(true);
            });
        } else {
            comments.forEach(c -> {
                validationService.checkCommentStatusChangeAdmin(newStatus, c.getStatus());
                c.setStatus(newStatus);
                c.setReasonDelete(statusUpdateRequest.getReasonDelete());
                c.setStatusEditor(null);
                c.setStatusChangedByAdmin(true);
            });
        }

        commentRepository.saveAll(comments);

        log.info("Админ изменил статус комментариев {}", comments);
        return comments.stream()
                .map(CommentMapper::toCommentFullAdminDto)
                .collect(Collectors.toList());
    }

    // event owner
    @Override
    public CommentFullEventOwnerDto getEventCommentByIdForEventOwner(Long userId, Long eventId, Long commentId) {
        log.info("Получаем комментарий id {} для события id {} пользователя id {}", eventId, userId, commentId);

        validationService.checkUserExists(userId);
        validationService.validateEventExistsAndInitiator(eventId, userId);

        Comment comment = getCommentOrThrow(
                commentRepository.findByIdAndEventId(commentId, eventId), commentId);

        validationService.checkAndGetCommentStatusForEventOwner(comment.getStatus().name());

        log.info("Для пользователя id {} получили комментарий для события id {}: {}", userId, eventId, comment);
        return CommentMapper.toCommentFullEventOwnerDto(comment);
    }

    @Override
    public List<CommentFullEventOwnerDto> getEventCommentsForEventOwner(Long userId, Long eventId, String status,
                                                                        int from, int size) {
        log.info("Получаем комментарии для события id {} пользователя id {}, status {}, from {} size {}",
                eventId, userId, status, from, size);

        validationService.checkUserExists(userId);
        validationService.validateEventExistsAndInitiator(eventId, userId);
        Set<CommentStatus> commentStatuses = validationService.checkAndGetCommentStatusForEventOwner(status);

        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        List<Comment> comments = commentRepository.findByEventIdAndStatusIn(eventId, commentStatuses, pageable).getContent();

        log.info("Для пользователя id {} получили комментарии для события id {}: {}", userId, comments, eventId);
        return comments.stream()
                .map(CommentMapper::toCommentFullEventOwnerDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentFullEventOwnerDto> getAllEventCommentsForEventOwner(Long userId, Set<String> statuses, int from, int size) {
        log.info("Получаем комментарии для всех событий пользователя id {}, statuses {}, from {} size {}",
                userId, statuses, from, size);

        validationService.checkUserExists(userId);

        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        List<Comment> comments;
        Set<CommentStatus> commentStatuses;
        if (statuses == null || statuses.isEmpty()) {
            commentStatuses = validationService.getCommentStatusEventOwnerAccessible();
        } else {
            commentStatuses = statuses.stream()
                    .map(s -> {
                        CommentStatus cs = validationService.checkAndGetCommentStatus(s);
                        validationService.checkCommentStatusAccessibleForEventOwner(cs);
                        return cs;
                    })
                    .collect(Collectors.toSet());
        }
        comments = commentRepository.findByUserIdAndStatuses(userId, commentStatuses, pageable).getContent();

        log.info("Для пользователя id {} получили комментарии для всех событий: {}", userId, comments);
        return comments.stream()
                .map(CommentMapper::toCommentFullEventOwnerDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CommentFullEventOwnerDto> changeCommentStatusByEventOwner(
            Long userId, Long eventId, CommentStatusUpdateRequest statusUpdateRequest) {
        log.info("У события id {} его создатель id {} меняет статус комментариев {}",
                eventId, userId, statusUpdateRequest);

        validationService.checkReasonDeleteIsGiven(statusUpdateRequest);
        User user = userService.getUserById(userId);
        validationService.validateEventExistsAndInitiator(eventId, userId);

        CommentStatus newStatus = validationService.checkAndGetCommentStatus(statusUpdateRequest.getStatus());

        List<Comment> comments = commentRepository.findAllByIdWithLock(
                statusUpdateRequest.getCommentIds());

        validationService.checkCommentsBelongEvent(comments, eventId);

        if (newStatus.equals(CommentStatus.POSTED)) {
            comments.forEach(c -> {
                validationService.checkAndGetCommentStatusForEventOwner(c.getStatus().name());
                validationService.checkCommentStatusChangeEventOwner(newStatus, c.getStatus());
                c.setStatus(newStatus);
                c.setStatusEditor(user);
                c.setPostedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            });
        } else {
            comments.forEach(c -> {
                validationService.checkAndGetCommentStatusForEventOwner(c.getStatus().name());
                validationService.checkCommentStatusChangeEventOwner(newStatus, c.getStatus());
                c.setStatus(newStatus);
                c.setStatusEditor(user);
                c.setReasonDelete(statusUpdateRequest.getReasonDelete());
            });
        }

        commentRepository.saveAll(comments);

        log.info("Пользователь id {} изменил статус комментариев {}", userId, comments);
        return comments.stream()
                        .map(CommentMapper::toCommentFullEventOwnerDto)
                        .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Long> getCountNumberOfCommentsForEvent(List<Long> eventIds) {
        return commentRepository.countByEventIdGrouped(eventIds).stream()
                .collect(Collectors.toMap(
                        CommentsCountProjection::getEventId,
                        CommentsCountProjection::getCount));
    }

    private Comment getCommentOrThrow(Optional<Comment> commentOpt, Long commentId) {
        return commentOpt
                .orElseThrow(() -> new NotFoundException(
                        String.format(NotFound.COMMENT, commentId)));
    }
}