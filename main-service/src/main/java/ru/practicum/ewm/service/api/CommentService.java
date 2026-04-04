package ru.practicum.ewm.service.api;

import ru.practicum.ewm.dto.request.CommentStatusUpdateRequest;
import ru.practicum.ewm.dto.request.NewCommentDto;
import ru.practicum.ewm.dto.request.UpdateCommentRequest;
import ru.practicum.ewm.dto.response.CommentFullAdminDto;
import ru.practicum.ewm.dto.response.CommentFullCommenterDto;
import ru.practicum.ewm.dto.response.CommentFullEventOwnerDto;
import ru.practicum.ewm.dto.response.CommentShortDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CommentService {

    CommentFullCommenterDto getCommentByIdForCommenter(Long userId, Long commentId);

    List<CommentFullCommenterDto> getCommentsForCommenter(Long userId, Set<String> statuses, int from, int size);

    CommentShortDto addComment(Long userId, NewCommentDto newCommentDto);

    CommentFullCommenterDto updateComment(Long userId, Long commentId, UpdateCommentRequest updateCommentRequest);

    List<CommentFullCommenterDto> changeCommentStatusByCommenter(Long userId,
                                                                 CommentStatusUpdateRequest statusUpdateRequest);

    CommentFullAdminDto getCommentByIdForAdmin(Long commentId);

    List<CommentFullAdminDto> getCommentsForAdmin(List<Long> commentIds, Set<String> statuses, int from, int size);

    List<CommentFullAdminDto> getEventCommentsForAdmin(Long eventId, Set<String> statuses, int from, int size);

    List<CommentFullAdminDto> changeCommentStatusByAdmin(CommentStatusUpdateRequest statusUpdateRequest);

    CommentFullEventOwnerDto getEventCommentByIdForEventOwner(Long userId, Long eventId, Long commentId);

    List<CommentFullEventOwnerDto> getEventCommentsForEventOwner(Long userId, Long eventId, String status,
                                                                 int from, int size);

    List<CommentFullEventOwnerDto> getAllEventCommentsForEventOwner(Long userId, Set<String> statuses,
                                                                   int from, int size);

    List<CommentFullEventOwnerDto> changeCommentStatusByEventOwner(Long userId, Long eventId,
                                                                   CommentStatusUpdateRequest statusUpdateRequest);

    Map<Long, Long> getCountNumberOfCommentsForEvent(List<Long> eventIds);
}
