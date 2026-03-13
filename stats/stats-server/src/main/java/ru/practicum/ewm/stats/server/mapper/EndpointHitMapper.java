package ru.practicum.ewm.stats.server.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.server.model.EndpointHitEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointHitMapper {

    public static EndpointHitEntity toEntity(EndpointHit dto) {
        return EndpointHitEntity.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }
}
