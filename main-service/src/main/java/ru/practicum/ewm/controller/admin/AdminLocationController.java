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

@RestController
@Slf4j
@RequestMapping(path = "/admin/locations")
@RequiredArgsConstructor
@Validated
public class AdminLocationController {
    private final PrintLogs printLogs;
    private final LocationService locationService;

    @GetMapping(path = "/{id}")
    public LocationDto getLocation(@PathVariable @Positive long id, HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Administrator is getting location by ID: {}", id);
        return locationService.getLocation(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto AdminAddLocation(@RequestBody @Valid NewLocationDto newLocationDto,
                                        HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        String message = "Administrator is adding location";
        printLogs.printObject(newLocationDto, message);
        return locationService.adminAddLocation(newLocationDto);
    }

    @PatchMapping(path = "/{id}")
    public LocationDto adminUpdateLocation(@RequestBody @Valid UpdateLocationDto patch,
                                           @PathVariable @Positive long id,
                                           HttpServletRequest httpServletRequest) {

        printLogs.printUrl(httpServletRequest);
        String message = String.format("Administrator is updating location ID: %s by patch", id);
        printLogs.printObject(patch, message);
        return locationService.adminUpdateLocation(id, patch);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminRemoveLocation(@PathVariable @Positive long id, HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Administrator is deleting location ID {}", id);
        locationService.adminRemoveLocation(id);
    }
}
