package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.*;

import java.util.List;

public interface EventService {
    EventFullDto initiatorAddEvent(long userId, NewEventDto newEventDto);

    EventFullDto initiatorUpdateEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventShortDto> initiatorGetEvents(long userId, Pageable page);

    EventShortDto initiatorGetEvent(long userId, long eventId);

    List<ParticipationRequestDto> initiatorGetEventRequests(long userId, long eventId);

    EventRequestStatusUpdateRequest initiatorChangeRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest changeRequest);
}
