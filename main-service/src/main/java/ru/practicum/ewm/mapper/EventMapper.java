package ru.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.request.NewEventDto;
import ru.practicum.ewm.dto.request.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.request.UpdateEventUserRequest;
import ru.practicum.ewm.dto.response.EventAnnotationDto;
import ru.practicum.ewm.dto.response.EventFullDto;
import ru.practicum.ewm.dto.response.EventShortDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.EventState;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {
    private static final Integer PARTICIPANT_LIMIT_DEFAULT_VALUE = 0;
    private static final Long CONFIRMED_REQUESTS_DEFAULT_VALUE = 0L;
    private static final Boolean REQUEST_MODERATION_DEFAULT_VALUE = true;
    private static final Boolean PAID_DEFAULT_VALUE = false;
    public static final Long NO_VIEWS = 0L;
    public static final Long NO_COMMENTS = 0L;

    public static Event toEvent(NewEventDto newEventDto, User initiator, Category category) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .confirmedRequests(CONFIRMED_REQUESTS_DEFAULT_VALUE)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .location(LocationMapper.toLocation(newEventDto.getLocation()))
                .paid(newEventDto.getPaid() != null ? newEventDto.getPaid() : PAID_DEFAULT_VALUE)
                .participantLimit(newEventDto
                        .getParticipantLimit() != null ? newEventDto.getParticipantLimit() : PARTICIPANT_LIMIT_DEFAULT_VALUE)
                .requestModeration(newEventDto
                        .getRequestModeration() != null ? newEventDto.getRequestModeration() : REQUEST_MODERATION_DEFAULT_VALUE)
                .state(EventState.PENDING)
                .title(newEventDto.getTitle())
                .build();
    }

    public static EventAnnotationDto toEventAnnotationDto(Event event) {
        return EventAnnotationDto.builder()
                .eventId(event.getId())
                .annotation(event.getAnnotation())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event, Long views, Long comments) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .comments(comments)
                .build();
    }

    public static EventFullDto toEventFullDto(Event event, Long views, Long comments) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().name())
                .title(event.getTitle())
                .views(views)
                .comments(comments)
                .build();
    }

    public static void updateEventFieldsByUser(Event event, UpdateEventUserRequest updateRequest) {

        if (updateRequest.hasAnnotation()) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.hasDescription()) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.hasLocation()) {
            event.setLocation(LocationMapper.toLocation(updateRequest.getLocation()));
        }

        if (updateRequest.hasPaid()) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.hasParticipantLimit()) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.hasRequestModeration()) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.hasTitle()) {
            event.setTitle(updateRequest.getTitle());
        }
    }

    public static void updateEventFieldsByAdmin(Event event, UpdateEventAdminRequest updateRequest) {

        if (updateRequest.hasAnnotation()) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.hasDescription()) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.hasLocation()) {
            event.setLocation(LocationMapper.toLocation(updateRequest.getLocation()));
        }

        if (updateRequest.hasPaid()) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.hasParticipantLimit()) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.hasRequestModeration()) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.hasTitle()) {
            event.setTitle(updateRequest.getTitle());
        }
    }
}