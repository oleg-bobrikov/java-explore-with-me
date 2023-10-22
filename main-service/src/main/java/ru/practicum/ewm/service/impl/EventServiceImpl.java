package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.WrongStateException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;

import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.EventService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final LocationMapper locationMapper;
    private final UserRepository userRepository;

    @Override
    public EventFullDto initiatorAddEvent(long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findUserById(userId);
        Category category = categoryRepository.findCategoryById(newEventDto.getCategory());
        Event event = eventMapper.toModel(newEventDto, category, initiator);

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto initiatorUpdateEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);
        if (event.getState() == EventState.PUBLISHED) {
            throw new WrongStateException("Only pending or canceled events can be changed for event with id = " + eventId);
        }
        // Immutable objects are used
        Event updatedEvent = applyPatch(event, updateEventUserRequest);
        return eventMapper.toEventFullDto(eventRepository.save(updatedEvent));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> initiatorGetEvents(long userId, Pageable page) {
        return eventRepository.findByInitiatorId(userId, page).getContent().stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    private Event applyPatch(Event event, UpdateEventUserRequest dto) {
        Event.EventBuilder eventBuilder = event.toBuilder();

        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            eventBuilder.annotation(dto.getAnnotation());
        }

        if (dto.getCategory() != null) {
            Category category = categoryRepository.findCategoryById(dto.getCategory());
            eventBuilder.category(category);
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            eventBuilder.description(dto.getDescription());
        }

        if (dto.getEventDate() != null) {
            eventBuilder.eventDate(dto.getEventDate());
        }

        if (dto.getLocation() != null) {
            eventBuilder.location(locationMapper.toModel(dto.getLocation()));
        }

        if (dto.getPaid() != null) {
            eventBuilder.paid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            eventBuilder.participantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            eventBuilder.requestModeration(dto.getRequestModeration());
        }

        if (dto.getStateAction() != null) {
            eventBuilder.state(dto.getStateAction());
        }

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            eventBuilder.title(dto.getTitle());
        }

        return eventBuilder.build();
    }


}
