package ru.practicum.ewm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentStatusUpdateRequest {

    @NotEmpty
    private List<Long> commentIds;

    @NotBlank
    private String status;

    @Size(min = 10, max = 200)
    private String reasonDelete;
}