package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {
    EventFullDto initiatorAddEvent(long userId, NewEventDto newEventDto);

    EventFullDto initiatorUpdateEvent(long userId, long eventId, UpdateEventByInitiatorDto updateEventUserRequest);

    List<EventShortDto> initiatorGetEvents(long userId, Pageable page);

    EventShortDto initiatorGetEvent(long userId, long eventId);

    List<ParticipationRequestDto> initiatorGetEventRequests(long userId, long eventId);

    UpdateParticipationRequestByInitiatorResultDto initiatorChangeRequestStatus(long userId, long eventId, UpdateParticipationRequestByInitiatorDto changeRequest);


    List<EventShortDto> findEvents(EventPublicFilterDto filter);

    EventFullDto updateEventByAdmin(long eventId, UpdateEventByAdminDto changeRequestDto);

    List<EventFullDto> adminFindEvents(Set<Long> users,
                                       Set<Event.State> states,
                                       Set<Long> categories,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Pageable page);

    EventFullDto findPublishedEventById(long id, String uri, String ip);

    List<EventShortDto> mapToEventShortDto(List<Event> events);

    List<EventShortDto> mapToEventShortDto(List<Event> events, Event.Sort sort);
}
