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
public class UpdateCommentRequest {
    @NotBlank(message = "Field: text. Error: must not be blank")
    @Size(min = 10, max = 2000, message = "Field: text. Error: длина должна быть от 10 до 2000 символов")
    private String text;
}