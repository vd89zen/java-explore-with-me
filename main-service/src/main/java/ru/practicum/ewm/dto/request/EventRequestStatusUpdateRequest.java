package ru.practicum.ewm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.enums.RequestStatusAction;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatusAction status;
}