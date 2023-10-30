package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;

import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.dto.ParticipationRequestConfirmation;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.stats.dto.EndpointHitRequestDto;
import ru.practicum.ewm.stats.dto.ViewStatsResponseDto;
import ru.practicum.ewm.model.Event.EventBuilder;

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
        Event savedEvent = eventRepository.save(event);
        return mapToEventFullDto(List.of(savedEvent)).get(0);
    }

    @Override
    public EventFullDto initiatorUpdateEvent(long userId, long eventId, UpdateEventByInitiatorDto patch) {
        userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);
        if (event.getState().equals(Event.State.PUBLISHED)) {
            throw new WrongStateException("Only pending or canceled events can be changed");
        }
        Event updatedEvent = applyPatch(event, patch);
        Event savedEvent = eventRepository.save(updatedEvent);
        return mapToEventFullDto(List.of(savedEvent)).get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> initiatorGetEvents(long userId, Pageable page) {
        List<Event> events = eventRepository.findByInitiatorId(userId, page);
        return mapToEventShortDto(events, Event.Sort.EVENT_DATE);
    }


    @Override
    public EventShortDto initiatorGetEvent(long userId, long eventId) {
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with ID %s not found for user with ID %s", eventId, userId)));
        return mapToEventShortDto(List.of(event), Event.Sort.EVENT_DATE).get(0);
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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UpdateParticipationRequestByInitiatorResultDto initiatorChangeRequestStatus(
            long userId, long eventId, UpdateParticipationRequestByInitiatorDto changeRequest) {

        userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);

        if (event.getState() != Event.State.PUBLISHED) {
            throw new WrongStateException("The event must be in the PUBLISHED state.");
        }

        int participantLimit = event.getParticipantLimit();

        if (participantLimit == 0 || !event.getRequestModeration()) {
            throw new BadRequestException("Change request confirmation is not required.");
        }

        boolean isConfirmedRequired = changeRequest.getStatus().equals(UpdateParticipationRequestByInitiatorDto.Status.CONFIRMED);
        if (isConfirmedRequired) {
            int confirmedParticipants = requestRepository.confirmedParticipantsByEvent(event);
            int required = confirmedParticipants + changeRequest.getRequestIds().size() - participantLimit;
            if (required > 0) {
                throw new ParticipantRequestException("\n" +
                        "It is necessary to increase participant limit to confirm requests by " + required);
            }
        }


        for (long requestId : changeRequest.getRequestIds()) {
            ParticipationRequest oldRequest = requestRepository.findRequestById(requestId);
            ParticipationRequest newRequest = oldRequest.toBuilder()
                    .status(isConfirmedRequired
                            ? ParticipationRequest.Status.CONFIRMED
                            : ParticipationRequest.Status.REJECTED)
                    .build();
            requestRepository.save(newRequest);
        }

        return UpdateParticipationRequestByInitiatorResultDto.builder()
                .requestIds(changeRequest.getRequestIds())
                .status(isConfirmedRequired
                        ? UpdateParticipationRequestByInitiatorResultDto.Status.CONFIRMED
                        : UpdateParticipationRequestByInitiatorResultDto.Status.REJECTED)
                .build();
    }

    @Override
    public List<EventShortDto> findEvents(EventPublicFilterDto filter) {
        LocalDateTime rangeStart = filter.getRangeStart();
        LocalDateTime rangeEnd = filter.getRangeEnd();

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new PeriodValidationException("RangeStart cannot be later than rangeEnd");
        }

        List<Event> events = eventRepository.findAllByFilter(
                filter.getText(),
                filter.getCategoryIds(),
                filter.getPaid(),
                rangeStart,
                rangeEnd,
                filter.isOnlyAvailable(),
                filter.getPage());

        sendToStats(filter.getUri(), filter.getIp());

        return mapToEventShortDto(events, filter.getSort());
    }

    @Override
    public List<EventFullDto> adminFindEvents(Set<Long> userIds,
                                              Set<Event.State> states,
                                              Set<Long> categoryIds,
                                              LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd,
                                              Pageable page) {
        List<Event> events = eventRepository.findByAdmin(userIds, states, categoryIds, rangeStart, rangeEnd, page);
        return mapToEventFullDto(events);
    }

    @Override
    public EventFullDto updateEventByAdmin(long eventId, UpdateEventByAdminDto patch) {
        Event originalEvent = eventRepository.findEventById(eventId);
        Event updatedEvent = applyPatch(originalEvent, patch);
        Event savedEvent = eventRepository.save(updatedEvent);

        return mapToEventFullDto(List.of(savedEvent)).get(0);
    }

    private List<EventShortDto> mapToEventShortDto(List<Event> events, Event.Sort sort) {
        Set<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toSet());

        Map<Long, Long> eventsViews = getViews(eventIds);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventIds);

        Comparator<EventShortDto> comparator;
        if (sort == null) {
            comparator = Comparator.comparing(EventShortDto::getId);
        } else if (sort.equals(Event.Sort.EVENT_DATE)) {
            comparator = Comparator.comparing(EventShortDto::getEventDate);
        } else if (sort.equals(Event.Sort.VIEWS)) {
            comparator = Comparator.comparing(EventShortDto::getViews).reversed();
        } else {
            comparator = Comparator.comparing(EventShortDto::getId);
        }

        return events.stream()
                .map(event -> eventMapper.toEventShortDto(event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        eventsViews.getOrDefault(event.getId(), 0L)))
                .sorted(comparator)
                .collect(Collectors.toList());
    }


    private List<EventFullDto> mapToEventFullDto(List<Event> events) {
        Set<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toSet());

        Map<Long, Long> eventsViews = getViews(eventIds);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventIds);

        return events.stream()
                .map(event -> eventMapper.toEventFullDto(event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        eventsViews.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private Map<Long, Long> getViews(Set<Long> eventIds) {
        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        Optional<LocalDateTime> start = eventRepository.getFirstPublicationDate(eventIds);

        if (start.isEmpty()) {
            return Collections.emptyMap();
        }

        List<ViewStatsResponseDto> response = statsClient.getStatistics(start.get(), LocalDateTime.now(), uris, true);

        return response.stream()
                .collect(Collectors.toMap(
                        dto -> Long.parseLong(dto.getUri().split("/")[2]),
                        ViewStatsResponseDto::getHits));
    }

    private Map<Long, Long> getConfirmedRequests(Set<Long> eventIds) {
        List<ParticipationRequestConfirmation> confirmationList = requestRepository.findAllConfirmedByEventIdIn(eventIds);

        return confirmationList.stream()
                .collect(Collectors.toMap(
                        ParticipationRequestConfirmation::getId,
                        ParticipationRequestConfirmation::getConfirmedRequests
                ));
    }

    private void applyCommonPatch(EventBuilder eventBuilder, UpdateEventDto patch) {
        if (patch.getAnnotation() != null) {
            eventBuilder.annotation(patch.getAnnotation());
        }

        if (patch.getCategory() != null) {
            eventBuilder.category(categoryRepository.findCategoryById(patch.getCategory()));
        }

        if (patch.getDescription() != null && !patch.getDescription().isBlank()) {
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

        if (patch.getRequestModeration() != null) {
            eventBuilder.requestModeration(patch.getRequestModeration());
        }

        if (patch.getTitle() != null && !patch.getTitle().isBlank()) {
            eventBuilder.title(patch.getTitle());
        }
    }

    private Event applyPatch(Event event, UpdateEventByAdminDto patch) {
        EventBuilder eventBuilder = event.toBuilder();
        applyCommonPatch(eventBuilder, patch);

        if (patch.getStateAction() != null) {
            Event.State newState = patch.getStateAction().equals(PUBLISH_EVENT) ? Event.State.PUBLISHED : Event.State.CANCELED;

            if (patch.getStateAction().equals(PUBLISH_EVENT) && !event.getState().equals(Event.State.PENDING)) {
                throw new WrongStateException("Event should be in PENDING state");
            } else if (patch.getStateAction().equals(REJECT_EVENT) && event.getState().equals(Event.State.PUBLISHED)) {
                throw new WrongStateException("Event should be not PUBLISHED");
            }

            eventBuilder.state(newState);
        }

        return eventBuilder.build();
    }


    private Event applyPatch(Event event, UpdateEventByInitiatorDto patch) {
        EventBuilder eventBuilder = event.toBuilder();

        applyCommonPatch(eventBuilder, patch);

        if (patch.getStateAction() != null) {
            switch (patch.getStateAction()) {
                case SEND_TO_REVIEW:
                    if (event.getState().equals(Event.State.PUBLISHED)) {
                        throw new WrongStateException("It is allowed to change only CANCELED OR PENDING events.");
                    }
                    eventBuilder.state(Event.State.PENDING);
                    break;
                case CANCEL_REVIEW:
                    eventBuilder.state(Event.State.CANCELED);
                    break;
            }
        }

        return eventBuilder.build();
    }

    @Override
    public EventFullDto findPublishedEventById(long id, String uri, String ip) {

        Event event = eventRepository.findPublishedEventById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format("No published event found with identifier %s", id)));
        log.info("Trying to send request {} from ip {} to statistic service", uri, ip);
        sendToStats(uri, ip);

        return mapToEventFullDto(List.of(event)).get(0);
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
