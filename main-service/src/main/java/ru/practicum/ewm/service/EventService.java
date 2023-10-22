package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.dto.UpdateEventUserRequest;

import java.util.List;

public interface EventService {
    EventFullDto initiatorAddEvent(long userId, NewEventDto newEventDto);

    EventFullDto initiatorUpdateEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventShortDto> initiatorGetEvents(long userId, Pageable page);
}
