package ru.practicum.ewm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventAnnotationDto {
    private Long eventId;
    private String annotation;
}
