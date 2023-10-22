package ru.practicum.ewm.controller.initiator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.dto.UpdateEventUserRequest;
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
            log.info("Initiator add event: {}", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getLocalizedMessage());
        }

        return eventService.initiatorAddEvent(userId, newEventDto);
    }

    @PatchMapping(path = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto initiatorAddEvent(@PathVariable long userId,
                                          @PathVariable long eventId,
                                          @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        try {
            String json = objectMapper.writeValueAsString(updateEventUserRequest);
            log.info("Initiator update event: {}", json);
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
        log.info("Get all events by user with id = {}, page from = {}, size = {}", userId, from, size);
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        return eventService.initiatorGetEvents(userId, page);
    }


}
