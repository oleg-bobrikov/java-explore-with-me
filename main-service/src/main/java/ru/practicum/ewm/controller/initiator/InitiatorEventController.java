package ru.practicum.ewm.controller.initiator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.util.PrintLogs;

import javax.servlet.http.HttpServletRequest;
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
@Validated
public class InitiatorEventController {
    private final EventService eventService;
    private final PrintLogs printLogs;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto initiatorAddEvent(@PathVariable @Positive long userId,
                                          @RequestBody @Valid NewEventDto newEventDto,
                                          HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        printLogs.printObject(newEventDto, "Initiator add event");

        EventFullDto savedEventFullDto = eventService.initiatorAddEvent(userId, newEventDto);
        printLogs.printObject(savedEventFullDto, "Initiator has added event");

        return savedEventFullDto;
    }

    @PatchMapping(path = "/{eventId}")
    public EventFullDto initiatorUpdateEvent(@PathVariable @Positive long userId,
                                             @PathVariable @Positive long eventId,
                                             @RequestBody @Valid UpdateEventByInitiatorDto updateEventUserRequest,
                                             HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        printLogs.printObject(updateEventUserRequest, "Initiator is updating event");

        EventFullDto savedEventFullDto = eventService.initiatorUpdateEvent(userId, eventId, updateEventUserRequest);
        printLogs.printObject(savedEventFullDto, "Initiator has updated event");

        return savedEventFullDto;
    }

    @GetMapping
    public List<EventShortDto> initiatorGetEvents(@PathVariable @Positive long userId,
                                                  @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size,
                                                  HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Initiator is getting all events by user ID: {}, page from = {}, size = {}", userId, from, size);
        return eventService.initiatorGetEvents(userId, from, size);
    }

    @GetMapping(path = "/{eventId}")
    public EventFullDto initiatorGetEvent(@PathVariable @Positive long userId,
                                          @PathVariable @Positive long eventId,
                                          HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Initiator is getting event by user ID: {}, event ID: {}", userId, eventId);
        return eventService.initiatorGetEvent(userId, eventId);
    }

    @GetMapping(path = "/{eventId}/requests")
    public List<ParticipationRequestDto> initiatorGetEventRequests(@PathVariable @Positive long userId,
                                                                   @PathVariable @Positive long eventId,
                                                                   HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Initiator is getting participation event requests by user ID: {}, event ID: {}", userId, eventId);
        return eventService.initiatorGetEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public UpdateParticipationRequestByInitiatorResultDto initiatorChangeRequestStatus(
            @PathVariable @Positive long userId,
            @PathVariable @Positive long eventId,
            @RequestBody @Valid UpdateParticipationRequestByInitiatorDto changeRequest,
            HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        String logMessage = String.format("Initiator ID: %d for event ID: %d is processing change request",
                userId, eventId);
        printLogs.printObject(changeRequest, logMessage);

        UpdateParticipationRequestByInitiatorResultDto savedRequest = eventService.initiatorChangeRequestStatus(userId, eventId, changeRequest);
        printLogs.printObject(savedRequest, "Initiator has changed request");

        return savedRequest;
    }

}
