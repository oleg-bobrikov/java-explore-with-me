package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.EventPublicFilterDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static ru.practicum.ewm.common.Constant.*;

@RestController
@Slf4j
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    List<EventShortDto> findEvents(@RequestParam(required = false) String text,
                                   @RequestParam(required = false) Set<Long> categories,
                                   @RequestParam(required = false) Boolean paid,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
                                   @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                   @RequestParam(required = false) Event.Sort sort,
                                   @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                   @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size,
                                   HttpServletRequest httpServletRequest) {

        log.info("{}: {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        log.info("Public user is finding events:");

        if (text != null) {
            log.info("searched by text: {}", text);
        }

        if (categories != null) {
            log.info("filtered by categories:");
            for (long categoryId : categories) {
                log.info("category ID: {}", categoryId);
            }
        }

        if (paid != null) {
            log.info("filtered by paid: {}", paid);
        }

        if (rangeStart != null) {
            log.info("range start: {}", rangeStart.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        }

        if (rangeEnd != null) {
            log.info("range end: {}", rangeEnd.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        }

        log.info("onlyAvailable: {}", onlyAvailable);

        if (sort != null) {
            log.info("sorted by: {}", sort);
        }

        log.info("from: {}", from);
        log.info("size: {}", size);

        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        EventPublicFilterDto filter = EventPublicFilterDto.builder()
                .text(text)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .categoryIds(categories)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .page(page)
                .paid(paid)
                .ip(httpServletRequest.getRemoteAddr())
                .uri(httpServletRequest.getRequestURI())
                .build();

        return eventService.findEvents(filter);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable long id, HttpServletRequest httpServletRequest) {
        log.info("{}: {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        log.info("Public user is getting event by ID: {}", id);
        return eventService.findPublishedEventById(id, httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());
    }

}
