package ru.practicum.ewm.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private HttpStatus status;
    private String timestamp;
}

