package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto addRequest(long userId, long eventId);

    List<ParticipationRequestDto> getRequests(long userId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);
}
