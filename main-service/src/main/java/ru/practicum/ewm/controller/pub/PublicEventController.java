package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.EventService;

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
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    List<EventFullDto> findEvents(@RequestParam(required = false) String text,
                                  @RequestParam(required = false) Set<Long> categories,
                                  @RequestParam(required = false) Boolean paid,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
                                  @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                  @RequestParam(required = false) Event.Sort sort,
                                  @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                  @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size) {

        Map<String, Object> parameters = new HashMap<>();
        log.info("Public user is finding events:");

        if (text != null) {
            log.info("searched by text: {}", text);
            parameters.put("text", text);
        }

        if (categories != null) {
            log.info("filtered by categories:");
            for (long categoryId : categories) {
                log.info("category ID: {}", categoryId);
            }
            parameters.put("categories", categories);
        }

        if (paid != null) {
            log.info("filtered by paid: {}", paid);
            parameters.put("paid", paid);
        }

        if (rangeStart != null) {
            log.info("range start: {}", rangeStart.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
            parameters.put("rangeStart", rangeStart);
        }

        if (rangeEnd != null) {
            log.info("range end: {}", rangeEnd.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
            parameters.put("rangeEnd", rangeEnd);
        }

        log.info("onlyAvailable: {}", onlyAvailable);
        parameters.put("onlyAvailable", onlyAvailable);

        if (sort != null) {
            log.info("sorted by: {}", sort);
            parameters.put("sort", sort);
        }

        log.info("from: {}", from);
        parameters.put("from", from);

        log.info("size: {}", size);
        parameters.put("size", size);

        return eventService.findEvents(parameters);
    }
}
