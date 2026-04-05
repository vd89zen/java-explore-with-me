package ru.practicum.ewm.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.common.CommentsCountProjection;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.enums.CommentStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"event", "commenter"})
    Page<Comment> findByIdIn(List<Long> commentIds, Pageable pageable);

    @EntityGraph(attributePaths = {"event", "commenter"})
    Page<Comment> findByIdInAndStatusIn(List<Long> commentIds, Set<CommentStatus> statuses, Pageable pageable);

    @EntityGraph(attributePaths = {"event", "commenter"})
    Page<Comment> findByStatusIn(Set<CommentStatus> statuses, Pageable pageable);

    @EntityGraph(attributePaths = {"event", "commenter"})
    Optional<Comment> findByIdAndCommenterIdAndStatus(Long commentId, Long commenterId, CommentStatus commentStatus);

    @EntityGraph(attributePaths = {"event", "commenter"})
    Optional<Comment> findByIdAndCommenterId(Long commentId, Long commenterId);

    @EntityGraph(attributePaths = {"event", "commenter"})
    Page<Comment> findByCommenterId(Long commenterId, Pageable pageable);

    @EntityGraph(attributePaths = {"event", "commenter"})
    Page<Comment> findByCommenterIdAndStatusIn(
            Long commenterId, Set<CommentStatus> statuses, Pageable pageable);

    @EntityGraph(attributePaths = {"event", "commenter"})
    Optional<Comment> findByIdAndEventId(Long commentId, Long eventId);

    @EntityGraph(attributePaths = {"event", "commenter"})
    Page<Comment> findByEventId(Long eventId, Pageable pageable);

    @EntityGraph(attributePaths = {"event", "commenter"})
    Page<Comment> findByEventIdAndStatusIn(
            Long eventId, Set<CommentStatus> statuses, Pageable pageable);

    @Query("SELECT c FROM Comment c " +
            "WHERE c.event.initiator.id = :userId " +
            "AND c.status IN :statuses")
    Page<Comment> findByUserIdAndStatuses(@Param("userId") Long userId,
                                          @Param("statuses") Set<CommentStatus> statuses, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Comment c WHERE c.id IN :ids")
    List<Comment> findAllByIdWithLock(@Param("ids") List<Long> ids);

    @Query("SELECT c.event.id as eventId, COUNT(c) as count " +
            "FROM Comment c " +
            "WHERE c.event.id IN :eventIds " +
            "GROUP BY c.event.id")
    List<CommentsCountProjection> countByEventIdGrouped(@Param("eventIds") List<Long> eventIds);
}