package ru.practicum.ewm.service.api;

import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.response.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.response.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto addParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest statusUpdateRequest);

}