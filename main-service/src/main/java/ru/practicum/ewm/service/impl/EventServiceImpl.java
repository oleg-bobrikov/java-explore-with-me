package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;

import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.EventService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final ParticipationRequestRepository requestRepository;

    @Override
    public EventFullDto initiatorAddEvent(long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findUserById(userId);
        Category category = categoryRepository.findCategoryById(newEventDto.getCategory());
        Event event = eventMapper.toModel(newEventDto, category, initiator);

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto initiatorUpdateEvent(long userId, long eventId, UpdateEventByInitiatorDto updateEventUserRequest) {
        userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);
        if (event.getState() == Event.State.PUBLISHED) {
            throw new WrongStateException("Only pending or canceled events can be changed");
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

    @Override
    public EventShortDto initiatorGetEvent(long userId, long eventId) {
        return eventMapper.toEventShortDto(eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with ID %s not found for user with ID %s", eventId, userId))));

    }

    @Override
    public List<ParticipationRequestDto> initiatorGetEventRequests(long initiatorId, long eventId) {
        List<ParticipationRequestDto> requestDtoList = requestRepository
                .findAllByInitiatorIdAndEventId(initiatorId, eventId);
        if (requestDtoList.isEmpty()) {
            throw new NotFoundException("No participation requests found for the event with ID: " + eventId);
        }
        return requestDtoList;
    }

    @Override
    public UpdateParticipationRequestByInitiatorDto initiatorChangeRequestStatus(long userId, long eventId,
                                                                                 UpdateParticipationRequestByInitiatorDto changeRequest) {
        userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);
        int participantLimit = event.getParticipantLimit();

        if (participantLimit == 0 || !event.getRequestModeration()) {
            throw new BadRequestException("Change request confirmation is not required.");
        }

        if (event.getState() != Event.State.PENDING) {
            throw new ConflictException("The event must be in the PENDING state.");
        }
        if (changeRequest.getStatus().equals(UpdateParticipationRequestByInitiatorDto.Status.CONFIRMED)) {
            int totalParticipants = requestRepository.countParticipantsByEvent(event);
            if (participantLimit >= totalParticipants) {
                throw new ConflictException("The participation requests limit has been reached.");
            }
        }

        for (long requestId : changeRequest.getRequestIds()) {
             requestRepository.findRequestById(requestId);
        }
        return null;
    }

    @Override
    public List<EventFullDto> findEvents(Map<String, Object> parameters) {
        return new ArrayList<>();
    }

    @Override
    public EventFullDto adminUpdateEvent(long eventId, UpdateEventByAdminDto changeRequestDto) {
        return new EventFullDto();
    }

    @Override
    public List<EventFullDto> adminFindEvents(Map<String, Object> parameters) {
        return new ArrayList<>();
    }

    private Event applyPatch(Event event, UpdateEventByInitiatorDto dto) {
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
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW:
                    eventBuilder.state(Event.State.PENDING);
                    break;
                case CANCEL_REVIEW:
                    eventBuilder.state(Event.State.CANCELED);
                    break;
                default:
            }
        }


        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            eventBuilder.title(dto.getTitle());
        }

        return eventBuilder.build();
    }


}
