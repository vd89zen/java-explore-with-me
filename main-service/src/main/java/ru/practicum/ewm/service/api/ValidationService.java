package ru.practicum.ewm.service.api;

import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.model.enums.RequestStatus;

import java.time.LocalDateTime;

public interface ValidationService {

    // валидация события
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
}
