package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.LocationDto;
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
public class PublicLocationController {
    private final PrintLogs printLogs;
    private final LocationService locationService;
    @GetMapping
    public List<LocationDto> getLocations(@RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                           @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size,
                                           HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Get all location with page from = {} and size = {}", from, size);
        return locationService.getLocations(from, size);
    }

    @GetMapping(path = "/{id}")
    public LocationDto getLocation(@PathVariable @Positive long id, HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Get location by ID: {}", id);
        return locationService.getLocation(id);
    }
}
