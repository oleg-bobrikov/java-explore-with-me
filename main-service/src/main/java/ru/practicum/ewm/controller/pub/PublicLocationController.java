package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.LocationFilterDto;
import ru.practicum.ewm.service.LocationService;
import ru.practicum.ewm.util.PrintLogs;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.ewm.common.Constant.PAGE_DEFAULT_FROM;
import static ru.practicum.ewm.common.Constant.PAGE_DEFAULT_SIZE;

@RestController
@Slf4j
@RequestMapping(path = "/locations")
@RequiredArgsConstructor
@Validated
public class PublicLocationController {
    private final PrintLogs printLogs;
    private final LocationService locationService;

    @GetMapping(path = "/{id}")
    public LocationDto getLocation(@PathVariable @Positive long id, HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Get location type by ID: {}", id);
        return locationService.getLocation(id);
    }

    @GetMapping
    public List<LocationDto> findLocations(@RequestParam(defaultValue = "-90") float latMin,
                                    @RequestParam(defaultValue = "90") float latMax,
                                    @RequestParam(defaultValue = "-180") float lonMin,
                                    @RequestParam(defaultValue = "180") float lonMax,
                                    @RequestParam(required = false) Set<Long> types,
                                    @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                    @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size,
                                    HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Admin is finding locations:");
        log.info("searched by latitude min: {}", latMin);
        log.info("searched by latitude max: {}", latMax);
        log.info("searched by longitude min: {}", lonMin);
        log.info("searched by longitude max: {}", lonMax);
        log.info("page from: {}", from);
        log.info("page size: {}", size);
        if (types != null) {
            log.info("category IDs: {}", types.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }

        LocationFilterDto filter = LocationFilterDto.builder()
                .latMin(latMin)
                .latMax(latMax)
                .lonMin(lonMin)
                .lonMax(lonMax)
                .types(types)
                .from(from)
                .size(size)
                .build();

        return locationService.findLocations(filter);
    }
}
