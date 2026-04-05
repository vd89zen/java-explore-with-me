package ru.practicum.ewm.model.enums;

import java.util.Optional;

/**
 * <p>Жизненный цикл комментария:</p>
 * PENDING — изначальный статус.
 * UPDATED — статус при обновлении текста комментария, требует одобрения (аналогично pending).
 * POSTED — комментарий одобрен или восстановлен(админом).
 * REJECTED — комментарий отклонён.
 * DELETED — комментарий удалён (создателем, владельцем события, админом).
 * CENSURED — комментарий удален администратором по причине цензуры.
 */
public enum CommentStatus {
    PENDING,
    UPDATED,
    POSTED,
    REJECTED,
    DELETED,
    CENSURED;

    /**
     * Преобразует строку в соответствующий статус комментария (с игнорированием регистра).
     */
    public static Optional<CommentStatus> from(String status) {
        if (status == null) {
            return Optional.empty();
        }

        for (CommentStatus commentStatus : values()) {
            if (commentStatus.name().equalsIgnoreCase(status)) {
                return Optional.of(commentStatus);
            }
        }
        return Optional.empty();
    }
}