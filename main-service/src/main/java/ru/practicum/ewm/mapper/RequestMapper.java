package ru.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.response.ParticipationRequestDto;
import ru.practicum.ewm.model.ParticipationRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest entity) {
        return ParticipationRequestDto.builder()
                .id(entity.getId())
                .created(entity.getCreated())
                .event(entity.getEvent().getId())
                .requester(entity.getRequester().getId())
                .status(entity.getStatus().name())
                .build();
    }
}