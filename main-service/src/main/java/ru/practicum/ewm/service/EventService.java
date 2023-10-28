package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.*;

import java.util.List;
import java.util.Map;

public interface EventService {
    EventFullDto initiatorAddEvent(long userId, NewEventDto newEventDto);

    EventFullDto initiatorUpdateEvent(long userId, long eventId, UpdateEventByInitiatorDto updateEventUserRequest);

    List<EventShortDto> initiatorGetEvents(long userId, Pageable page);

    EventShortDto initiatorGetEvent(long userId, long eventId);

    List<ParticipationRequestDto> initiatorGetEventRequests(long userId, long eventId);

    UpdateParticipationRequestByInitiatorDto initiatorChangeRequestStatus(long userId, long eventId, UpdateParticipationRequestByInitiatorDto changeRequest);

    List<EventFullDto> findEvents(Map<String, Object> parameters);

    EventFullDto adminUpdateEvent(long eventId, UpdateEventByAdminDto changeRequestDto);

    List<EventFullDto> adminFindEvents(Map<String, Object> parameters);
}
