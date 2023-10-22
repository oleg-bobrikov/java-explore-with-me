package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto participantAddRequest(long userId, long eventId);

    List<ParticipationRequestDto> participantGetRequests(long userId);
}
