package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.LocationTypeDto;
import ru.practicum.ewm.service.LocationService;
import ru.practicum.ewm.util.PrintLogs;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.common.Constant.PAGE_DEFAULT_FROM;
import static ru.practicum.ewm.common.Constant.PAGE_DEFAULT_SIZE;

@RestController
@Slf4j
@RequestMapping(path = "/location/types")
@RequiredArgsConstructor
@Validated
public class PublicLocationTypeController {
    private final PrintLogs printLogs;
    private final LocationService locationService;

    @GetMapping(path = "/{id}")
    public LocationTypeDto adminGetLocationType(@PathVariable @Positive long id, HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Attempt to get location type  by ID {}", id);

        return locationService.adminGetLocationType(id);
    }

    @GetMapping
    public List<LocationTypeDto> findLocationTypes(@RequestParam(required = false) String text,
                                                   @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                                   @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size,
                                                   HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        if (text == null) {
            log.info("Attempt to get all location types ");
        } else {
            log.info("Attempt to search location types by text: {}", text);
        }

        return locationService.adminFindLocationTypes(text, from, size);
    }
}
