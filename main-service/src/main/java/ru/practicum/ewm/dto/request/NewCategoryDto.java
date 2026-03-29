package ru.practicum.ewm.dto.request;

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
public class NewCategoryDto {

    @NotBlank(message = "Field: name. Error: must not be blank")
    @Size(min = 1, max = 50, message = "Field: name. Error: длина должна быть от 1 до 50 символов")
    private String name;
}