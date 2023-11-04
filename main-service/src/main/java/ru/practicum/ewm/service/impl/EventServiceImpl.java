package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.stats.dto.EndpointHitRequestDto;
import ru.practicum.ewm.stats.dto.ViewStatsResponseDto;
import ru.practicum.ewm.model.Event.EventBuilder;
import ru.practicum.ewm.util.PageRequestHelper;

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
    private final ParticipationRequestMapper participationRequestMapper;
    private final LocationTypeRepository locationTypeRepository;

    @Value("${app.name}")
    private String app;

    @Override
    public EventFullDto initiatorAddEvent(long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findUserById(userId);
        Category category = categoryRepository.findCategoryById(newEventDto.getCategory());
        Long locationTypeId = newEventDto.getLocation().getType();
        LocationType locationType = locationTypeId == null
                ? null
                : locationTypeRepository.findLocationTypeById(locationTypeId);
        Location location = locationMapper.toModel(newEventDto.getLocation(), locationType);
        EventDto eventDto = eventMapper.toEventDto(newEventDto, initiator, category, location);

        Event event = eventMapper.toModel(eventDto);
        Event savedEvent = eventRepository.save(event);
        List<EventFullDto> eventFullDtoList = mapToEventFullDto(List.of(savedEvent));
        return eventFullDtoList.isEmpty() ? null : eventFullDtoList.get(0);
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
    public List<EventShortDto> initiatorGetEvents(long userId, int from, int size) {
        PageRequest page = PageRequestHelper.of(from, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, page);
        return mapToEventShortDto(events);
    }


    @Override
    public EventFullDto initiatorGetEvent(long userId, long eventId) {
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with ID %s not found for user with ID %s", eventId, userId)));
        return mapToEventFullDto(List.of(event)).get(0);
    }

    @Override
    public List<ParticipationRequestDto> initiatorGetEventRequests(long initiatorId, long eventId) {
        List<ParticipationRequest> requests = requestRepository
                .findAllByEventIdAndEventInitiatorId(eventId, initiatorId);

        if (requests.isEmpty()) {
            throw new NotFoundException("No participation requests found for the event with ID: " + eventId);
        }

        return participationRequestMapper.toDto(requests);
    }


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UpdateParticipationRequestByInitiatorResultDto initiatorChangeRequestStatus(
            long userId, long eventId, UpdateParticipationRequestByInitiatorDto changeRequest) {

        Event event = eventRepository.findEventById(eventId);
        checkEventState(event);

        boolean isConfirmation = changeRequest.getStatus() == UpdateParticipationRequestByInitiatorDto.Status.CONFIRMED;
        checkRequestConfirmation(event, changeRequest.getRequestIds(), isConfirmation);

        List<ParticipationRequest> oldRequests = findParticipationRequests(new HashSet<>(changeRequest.getRequestIds()));
        List<ParticipationRequestDto> updatedRequests = processParticipationRequests(oldRequests, isConfirmation);
        List<ParticipationRequestDto> confirmedRequests = isConfirmation ? updatedRequests : new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = !isConfirmation ? updatedRequests : new ArrayList<>();

        return UpdateParticipationRequestByInitiatorResultDto.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    @Override
    public List<EventShortDto> findEvents(EventPublicFilterDto filter) {
        validateCoordinates(filter.getLatitude(), filter.getLongitude());

        LocalDateTime rangeStart = getValidRangeStart(filter.getRangeStart());
        LocalDateTime rangeEnd = filter.getRangeEnd();

        validateDateRange(rangeStart, rangeEnd);

        PageRequest page = PageRequestHelper.of(filter.getFrom(), filter.getSize());
        List<Event> events = eventRepository.findAllByFilter(
                filter.getText(),
                filter.getCategoryIds(),
                filter.getPaid(),
                rangeStart,
                rangeEnd,
                filter.isOnlyAvailable(),
                filter.getLatitude(),
                filter.getLongitude(),
                filter.getRadiusInMeters(),
                page
        );

        sendToStats(filter.getUri(), filter.getIp());
        return mapToEventShortDto(events, filter.getSort());
    }


    @Override
    public List<EventFullDto> adminFindEvents(EventAdminFilterDto filter) {
        PageRequest page = PageRequestHelper.of(filter.getFrom(), filter.getSize());
        List<Event> events = eventRepository.findByAdmin(
                filter.getUsers(),
                filter.getStates(),
                filter.getCategories(),
                filter.getRangeStart(),
                filter.getRangeEnd(),
                page);
        return mapToEventFullDto(events);
    }

    @Override
    public EventFullDto updateEventByAdmin(long eventId, UpdateEventByAdminDto patch) {
        Event originalEvent = eventRepository.findEventById(eventId);
        Event updatedEvent = applyPatch(originalEvent, patch);
        Event savedEvent = eventRepository.save(updatedEvent);

        return mapToEventFullDto(List.of(savedEvent)).get(0);
    }

    @Override
    public List<EventShortDto> mapToEventShortDto(List<Event> events, Event.Sort sort) {
        Set<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toSet());

        Map<Long, Long> eventsViews = getViews(eventIds);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventIds);

        Comparator<EventShortDto> comparator;

        switch (sort) {
            case EVENT_DATE:
                comparator = Comparator.comparing(EventShortDto::getEventDate);
                break;
            case VIEWS:
                comparator = Comparator.comparing(EventShortDto::getViews).reversed();
                break;
            case ID:
            default:
                comparator = Comparator.comparing(EventShortDto::getId);
                break;
        }


        return events.stream()
                .map(event -> eventMapper.toEventShortDto(event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        eventsViews.getOrDefault(event.getId(), 0L)))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> mapToEventShortDto(List<Event> events) {
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

    private void validateCoordinates(Float latitude, Float longitude) {
        if ((latitude != null && longitude == null) || (longitude != null && latitude == null)) {
            throw new BadRequestException("Incorrect latitude or longitude value");
        }
    }

    private LocalDateTime getValidRangeStart(LocalDateTime rangeStart) {
        if (rangeStart == null) {
            return LocalDateTime.now();
        }
        return rangeStart;
    }

    private void validateDateRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new PeriodValidationException("RangeStart cannot be later than rangeEnd");
        }
    }

    private List<ParticipationRequestDto> processParticipationRequests(List<ParticipationRequest> oldRequests, boolean isConfirmation) {
        return oldRequests.stream()
                .map(oldRequest -> processRequest(oldRequest, isConfirmation))
                .map(participationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    private ParticipationRequest processRequest(ParticipationRequest oldRequest, boolean isConfirmation) {
        if (oldRequest.getStatus() == ParticipationRequest.Status.CONFIRMED && !isConfirmation) {
            throw new ParticipantRequestException("It's not allowed to reject a confirmed participation request");
        }

        ParticipationRequest newRequest = oldRequest.toBuilder()
                .status(isConfirmation ? ParticipationRequest.Status.CONFIRMED : ParticipationRequest.Status.REJECTED)
                .build();

        return (oldRequest.getStatus() != newRequest.getStatus()) ? requestRepository.save(newRequest) : oldRequest;
    }

    private List<ParticipationRequest> findParticipationRequests(Set<Long> ids) {
        List<ParticipationRequest> requests = requestRepository.findAllById(ids);
        Set<Long> foundIds = requests.stream().map(ParticipationRequest::getId).collect(Collectors.toSet());
        if (!ids.equals(foundIds)) {
            throw new NotFoundException("Not all participation requests found");
        }
        return requests;
    }

    private void checkEventState(Event event) {
        if (event.getState() != Event.State.PUBLISHED) {
            throw new WrongStateException("The event must be in the PUBLISHED state.");
        }

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new BadRequestException("Change request confirmation is not required.");
        }
    }

    private void checkRequestConfirmation(Event event, List<Long> requestIds, boolean isConfirmation) {
        if (isConfirmation) {
            int confirmedParticipants = requestRepository.confirmedParticipantsByEvent(event);
            int balance = event.getParticipantLimit() - confirmedParticipants - requestIds.size();
            if (balance < 0) {
                throw new ParticipantRequestException("It is necessary to increase participant limit to confirm requests by " + -balance);
            }
        }
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
                .filter(dto -> {
                    String[] uriParts = dto.getUri().split("/");
                    return uriParts.length >= 3;
                })
                .collect(Collectors.toMap(
                        dto -> Long.parseLong(dto.getUri().split("/")[2]),
                        ViewStatsResponseDto::getHits
                ));
    }

    private Map<Long, Long> getConfirmedRequests(Set<Long> eventIds) {
        List<ParticipationRequest> confirmedRequests = requestRepository
                .findAllByStatusAndEventIdIn(ParticipationRequest.Status.CONFIRMED, eventIds);

        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId(), Collectors.counting()));
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
            NewLocationDto newLocationDto = patch.getLocation();
            LocationType locationType = newLocationDto.getType() == null
                    ? null :
                    locationTypeRepository.findLocationTypeById(newLocationDto.getType());
            eventBuilder.location(locationMapper.toModel(newLocationDto, locationType));
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
}
