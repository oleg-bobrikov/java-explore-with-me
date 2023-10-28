package ru.practicum.ewm.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.UpdateEventByAdminDto;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.practicum.ewm.common.Constant.*;

@RestController
@Slf4j
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;
    private final ObjectMapper objectMapper;

    @GetMapping
    List<EventFullDto> adminFindEvents(@RequestParam(required = false) Set<Long> users,
                                       @RequestParam(required = false) Set<Event.State> states,
                                       @RequestParam(required = false) Set<Long> categories,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
                                       @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                       @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size) {

        Map<String, Object> parameters = new HashMap<>();
        log.info("admin is finding events:");

        if (users != null) {
            log.info("for users:");
            for (long userId : users) {
                log.info("user ID: {}", userId);
            }
            parameters.put("users", users);
        }

        if (states != null) {
            log.info("for states:");
            for (Event.State state : states) {
                log.info("state: {}", state);
            }
            parameters.put("states", states);
        }

        if (categories != null) {
            log.info("for categories:");
            for (long categoryId : categories) {
                log.info("category ID: {}", categoryId);
            }
            parameters.put("categories", categories);
        }

        if (rangeStart != null) {
            log.info("range start (admin request): {}", rangeStart.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
            parameters.put("rangeStart", rangeStart);
        }

        if (rangeEnd != null) {
            log.info("range end (admin request): {}", rangeEnd.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
            parameters.put("rangeEnd", rangeEnd);
        }

        log.info("from: {}", from);
        parameters.put("from", from);

        log.info("size: {}", size);
        parameters.put("size", size);

        return eventService.adminFindEvents(parameters);
    }

    @PatchMapping(path = "/{eventId}")
    EventFullDto adminUpdateEvent(@PathVariable long eventId, @Valid @RequestBody UpdateEventByAdminDto updateEventByAdminDto) {
        try {
            String json = objectMapper.writeValueAsString(updateEventByAdminDto);
            log.info("Administrator update event ID: {} by patch: {}", eventId, json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getLocalizedMessage());
        }

        return eventService.adminUpdateEvent(eventId, updateEventByAdminDto);
    }
}
