package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.LocationService;
import ru.practicum.ewm.util.PrintLogs;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.ewm.common.Constant.*;
import static ru.practicum.ewm.common.Constant.PAGE_DEFAULT_SIZE;

@RestController
@Slf4j
@RequestMapping(path = "/admin/location")
@RequiredArgsConstructor
@Validated
public class AdminLocationController {
    private final PrintLogs printLogs;
    private final LocationService locationService;

    @PostMapping(path = "/types")
    @ResponseStatus(HttpStatus.CREATED)
    public LocationTypeDto adminAddLocationType(@RequestBody @Valid NewLocationTypeDto newLocationTypeDto,
                                                HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Attempt to save location type with name {}", newLocationTypeDto.getName());

        return locationService.adminAddLocationType(newLocationTypeDto);
    }

    @PatchMapping(path = "/types/{id}")
    public LocationTypeDto adminUpdateLocationType(@RequestBody @Valid NewLocationTypeDto typeDto,
                                                   @PathVariable @Positive long id,
                                                   HttpServletRequest httpServletRequest) {

        printLogs.printUrl(httpServletRequest);
        log.info("Attempt to update the name of the location type with identifier {} to {}", id, typeDto.getName());

        return locationService.adminUpdateLocationType(id, typeDto);
    }

    @DeleteMapping(path = "/types/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminRemoveLocationType(@PathVariable @Positive long id, HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Attempt to delete location type  with identifier {}", id);

        locationService.adminRemoveLocationType(id);
    }

    @PatchMapping(path = "/{id}")
    public LocationDto adminUpdateLocation(@RequestBody @Valid UpdateLocationDto patch,
                                           @PathVariable @Positive long id,
                                           HttpServletRequest httpServletRequest) {

        printLogs.printUrl(httpServletRequest);
        log.info("Attempt to update location ID {}", id);
        String message = String.format("Administrator update location ID: %s by patch", id);
        printLogs.printObject(patch, message);
        return locationService.adminUpdateLocation(id, patch);
    }

    @GetMapping
    List<LocationDto> findLocations(@RequestParam(defaultValue = "-90") float latMin,
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

        return locationService.adminFindLocations(filter);
    }
}
