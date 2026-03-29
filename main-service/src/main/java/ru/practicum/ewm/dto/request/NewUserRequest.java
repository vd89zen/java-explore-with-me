package ru.practicum.ewm.dto.request;

import jakarta.validation.constraints.Email;
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
public class NewUserRequest {

    @NotBlank(message = "Field: name. Error: must not be blank")
    @Size(min = 2, max = 250, message = "Field: name. Error: длина должна быть от 2 до 250 символов")
    private String name;

    @NotBlank(message = "Field: email. Error: must not be blank")
    @Email(message = "Field: email. Error: must be a valid email")
    @Size(min = 6, max = 254, message = "Field: email. Error: длина должна быть от 6 до 254 символов")
    private String email;
}