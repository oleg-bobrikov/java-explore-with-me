package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventAdminFilterDto;
import ru.practicum.ewm.dto.UpdateEventByAdminDto;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.util.PrintLogs;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.ewm.common.Constant.*;

@RestController
@Slf4j
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
public class AdminEventController {
    private final EventService eventService;
    private final PrintLogs printLogs;

    @GetMapping
    List<EventFullDto> adminFindEvents(@RequestParam(required = false) Set<Long> users,
                                       @RequestParam(required = false) Set<Event.State> states,
                                       @RequestParam(required = false) Set<Long> categories,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
                                       @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                       @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size,
                                       HttpServletRequest httpServletRequest) {

        printLogs.printUrl(httpServletRequest);
        log.info("admin is finding events using filter by:");

        if (users != null) {
            log.info("user IDs: {}", users.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }

        if (states != null) {
            log.info("state IDs: {}", states.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }

        if (categories != null) {
            log.info("category IDs: {}", categories.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }

        if (rangeStart != null) {
            log.info("range start (admin request): {}", rangeStart.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        }

        if (rangeEnd != null) {
            log.info("range end (admin request): {}", rangeEnd.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        }

        log.info("from: {}", from);
        log.info("size: {}", size);

        EventAdminFilterDto filter = EventAdminFilterDto.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();

        return eventService.adminFindEvents(filter);
    }

    @PatchMapping(path = "/{eventId}")
    EventFullDto adminUpdateEvent(@PathVariable long eventId,
                                  @Valid @RequestBody UpdateEventByAdminDto updateEventByAdminDto,
                                  HttpServletRequest httpServletRequest) {

        printLogs.printUrl(httpServletRequest);
        String message = String.format("Administrator update event ID: %s by patch", eventId);
        printLogs.printObject(updateEventByAdminDto, message);

        return eventService.updateEventByAdmin(eventId, updateEventByAdminDto);
    }
}
