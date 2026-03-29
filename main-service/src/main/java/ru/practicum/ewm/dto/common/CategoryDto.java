package ru.practicum.ewm.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDto {
    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}