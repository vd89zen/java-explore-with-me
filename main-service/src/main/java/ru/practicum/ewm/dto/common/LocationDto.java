package ru.practicum.ewm.dto.common;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocationDto {

    @NotNull(message = "Field: lat. Error: must not be null")
    private Float lat;

    @NotNull(message = "Field: lon. Error: must not be null")
    private Float lon;
}