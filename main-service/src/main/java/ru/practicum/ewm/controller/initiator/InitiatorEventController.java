package ru.practicum.ewm.controller.initiator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.common.Constant.PAGE_DEFAULT_FROM;
import static ru.practicum.ewm.common.Constant.PAGE_DEFAULT_SIZE;

@RestController
@Slf4j
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class InitiatorEventController {
    private final EventService eventService;
    private final ObjectMapper objectMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto initiatorAddEvent(@PathVariable long userId, @RequestBody @Valid NewEventDto newEventDto) {
        try {
            String json = objectMapper.writeValueAsString(newEventDto);
            log.info("Initiator is adding event: {}", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getLocalizedMessage());
        }

        return eventService.initiatorAddEvent(userId, newEventDto);
    }

    @PatchMapping(path = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto initiatorUpdateEvent(@PathVariable long userId,
                                             @PathVariable long eventId,
                                             @RequestBody @Valid UpdateEventByInitiatorDto updateEventUserRequest) {
        try {
            String json = objectMapper.writeValueAsString(updateEventUserRequest);
            log.info("Initiator is updating event: {}", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getLocalizedMessage());
        }

        return eventService.initiatorUpdateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> initiatorGetEvents(@PathVariable long userId,
                                                  @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size) {
        log.info("Initiator is getting all events by user ID: {}, page from = {}, size = {}", userId, from, size);
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        return eventService.initiatorGetEvents(userId, page);
    }

    @GetMapping(path = " /{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventShortDto initiatorGetEvent(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Initiator is getting event by user ID: {}, event ID: {}", userId, eventId);
        return eventService.initiatorGetEvent(userId, eventId);
    }

    @GetMapping(path = " /{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> initiatorGetEventRequests(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Initiator is getting participation event requests by user ID: {}, event ID: {}", userId, eventId);
        return eventService.initiatorGetEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public UpdateParticipationRequestByInitiatorDto initiatorChangeRequestStatus(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody @Valid UpdateParticipationRequestByInitiatorDto changeRequest) {

        try {
            String json = objectMapper.writeValueAsString(changeRequest);
            String logMessage = String.format("Initiator ID: %d for event ID: %d is processing change request: %s",
                    userId, eventId, json);
            log.info(logMessage);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getLocalizedMessage());
        }

        return eventService.initiatorChangeRequestStatus(userId, eventId, changeRequest);
    }

}
