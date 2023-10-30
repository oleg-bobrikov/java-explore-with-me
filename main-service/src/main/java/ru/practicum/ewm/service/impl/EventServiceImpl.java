package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;

import ru.practicum.ewm.model.User;
import ru.practicum.ewm.projection.ParticipationRequestConfirmation;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.stats.dto.EndpointHitRequestDto;
import ru.practicum.ewm.stats.dto.ViewStatsResponseDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.dto.UpdateEventByAdminDto.StateAction.PUBLISH_EVENT;
import static ru.practicum.ewm.dto.UpdateEventByAdminDto.StateAction.REJECT_EVENT;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final LocationMapper locationMapper;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository requestRepository;
    private final StatsClient statsClient;

    @Value("${app.name}")
    private String app;

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
            throw new WrongStateException("The event must be in the PENDING state.");
        }
        if (changeRequest.getStatus().equals(UpdateParticipationRequestByInitiatorDto.Status.CONFIRMED)) {
            int totalParticipants = requestRepository.confirmedParticipantsByEvent(event);
            if (participantLimit >= totalParticipants) {
                throw new ParticipantRequestException("The participation requests limit has been reached.");
            }
        }

        for (long requestId : changeRequest.getRequestIds()) {
            requestRepository.findRequestById(requestId);
        }
        return null;
    }

    @Override
    public List<EventShortDto> findEvents(Map<String, Object> parameters) {
        LocalDateTime rangeStart = (LocalDateTime) parameters.get("rangeStart");
        LocalDateTime rangeEnd = (LocalDateTime) parameters.get("rangeEnd");

        String text = (String) parameters.get("text");
        String uri = (String) parameters.get("uri");
        String ip = (String) parameters.get("ip");

        boolean onlyAvailable = (boolean) parameters.get("onlyAvailable");
        Boolean paid = (Boolean) parameters.get("paid");

        Set<Long> categoryIds = (Set<Long>) parameters.get("categoryIds");

        Event.Sort sort = (Event.Sort) parameters.get("sort");

        Pageable page = (Pageable) parameters.get("page");

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new PeriodValidationException("RangeStart cannot be later than rangeEnd");
        }

        List<Event> events = eventRepository.findAllByFilter(text, categoryIds, paid, rangeStart, rangeEnd, onlyAvailable, page);

        sendToStats(uri, ip);

        return mapToEventShortDto(events, rangeStart, sort);
    }

    @Override
    public EventFullDto updateEventByAdmin(long eventId, UpdateEventByAdminDto patch) {
        Event originalEvent = eventRepository.findEventById(eventId);
        Event updatedEvent = applyPatch(originalEvent, patch);

        return eventMapper.toEventFullDto(eventRepository.save(updatedEvent));
    }

    private List<EventShortDto> mapToEventShortDto(List<Event> events, LocalDateTime start, Event.Sort sort) {
        Set<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toSet());


        Map<Long, Long> eventsViews = getViews(eventIds);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventIds);

        return events.stream()
                .map(event -> eventMapper.toEventShortDto(event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        eventsViews.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private Map<Long, Long> getViews(Set<Long> eventIds) {
        List<String> uris = eventIds
                .stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        Optional<LocalDateTime> start = eventRepository.getFirstPublicationDate(eventIds);

        Map<Long, Long> views = new HashMap<>();

        if (start.isPresent()) {
            List<ViewStatsResponseDto> response = statsClient.getStatistics(start.get(), LocalDateTime.now(), uris, true);
            response.forEach(dto -> {
                String uri = dto.getUri();
                String[] split = uri.split("/");
                String id = split[2];
                Long eventId = Long.parseLong(id);
                views.put(eventId, dto.getHits());
            });
        }

        return views;
    }

    private Map<Long, Long> getConfirmedRequests(Set<Long> eventIds) {
        List<ParticipationRequestConfirmation> confirmationList = requestRepository
                .findAllByStatusAndEventIdIn(eventIds);

        Map<Long, Long> confirmedRequests = new HashMap<>();

        confirmationList.forEach(confirmation -> confirmedRequests.put(confirmation.getId(), confirmation.getConfirmedRequests()));

        return confirmedRequests;
    }

    private Event applyPatch(Event event, UpdateEventByAdminDto patch) {
        Event.EventBuilder eventBuilder = event.toBuilder();

        if (patch.getAnnotation() != null) {
            eventBuilder.annotation(patch.getAnnotation());
        }

        if (patch.getCategory() != null) {
            eventBuilder.category(categoryRepository.findCategoryById(patch.getCategory()));
        }

        if (patch.getDescription() != null) {
            eventBuilder.description(patch.getDescription());
        }

        if (patch.getEventDate() != null) {
            eventBuilder.eventDate(patch.getEventDate());
        }

        if (patch.getLocation() != null) {
            eventBuilder.location(locationMapper.toModel(patch.getLocation()));
        }

        if (patch.getPaid() != null) {
            eventBuilder.paid(patch.getPaid());
        }

        if (patch.getParticipantLimit() != null) {
            eventBuilder.participantLimit(patch.getParticipantLimit());
        }

        if (patch.getTitle() != null && !patch.getTitle().isBlank()) {
            eventBuilder.title(patch.getTitle());
        }

        if (patch.getStateAction() == null) {
            return eventBuilder.build();
        }

        if (patch.getStateAction().equals(PUBLISH_EVENT) && !event.getState().equals(Event.State.PENDING)) {
            throw new WrongStateException("Event should be in PENDING state");
        } else if (patch.getStateAction().equals(REJECT_EVENT) && event.getState().equals(Event.State.PUBLISHED)) {
            throw new WrongStateException("Event should be not PUBLISHED");
        } else if (patch.getStateAction().equals(PUBLISH_EVENT)) {
            eventBuilder.state(Event.State.PUBLISHED);
        } else if (patch.getStateAction().equals(REJECT_EVENT)) {
            eventBuilder.state(Event.State.CANCELED);
        }

        return eventBuilder.build();
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
            if (event.getState().equals(Event.State.PUBLISHED)) {
                throw new WrongStateException("It is allowed to changed only CANCELED OR PENDING events.");
            }
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

    @Override
    public List<EventFullDto> adminFindEvents(Set<Long> userIds,
                                              Set<Event.State> states,
                                              Set<Long> categoryIds,
                                              LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd,
                                              Pageable page) {
        return eventMapper.toEventFullDto(eventRepository.findByAdmin(userIds, states, categoryIds, rangeStart, rangeEnd, page));
    }

    @Override
    public EventFullDto findPublishedEventById(long id, String uri, String ip) {

        Event event = eventRepository.findPublishedEventById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format("No published event found with identifier %s", id)));
        log.info("Trying to send request {} from ip {} to statistic service", uri, ip);
        sendToStats(uri, ip);

        return eventMapper.toEventFullDto(event);
    }

    private void sendToStats(String uri, String ip) {
        EndpointHitRequestDto endpointHitRequestDto = EndpointHitRequestDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();

        statsClient.createHit(endpointHitRequestDto);
    }
}
