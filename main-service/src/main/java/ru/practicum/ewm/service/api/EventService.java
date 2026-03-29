package ru.practicum.ewm.service.api;

import ru.practicum.ewm.dto.request.NewEventDto;
import ru.practicum.ewm.dto.request.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.request.UpdateEventUserRequest;
import ru.practicum.ewm.dto.response.EventFullDto;
import ru.practicum.ewm.dto.response.EventShortDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.enums.EventSort;
import ru.practicum.ewm.model.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EventService {

    // публичные методы
    List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        boolean onlyAvailable, EventSort sort, int from, int size);

    EventFullDto getEventByIdPublic(Long eventId);

    // приватные методы
    List<EventShortDto> getEventsPrivate(Long userId, int from, int size);

    EventFullDto addEventPrivate(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByIdPrivate(Long userId, Long eventId);

    EventFullDto updateEventByIdPrivate(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    // методы для админа
    List<EventFullDto> getEventsAdmin(List<Long> users, List<EventState> states,
                                      List<Long> categories, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventByIdAdmin(Long eventId, UpdateEventAdminRequest updateRequest);

    // вспомогательные методы
    Map<Long, Long> getViewStatsForEvents(List<Event> events);

    Event getEventByIdWithLock(Long eventId);

    List<Event> getAllEventById(Set<Long> ids);

    void updateConfirmedRequests(Long eventId);
}