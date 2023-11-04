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
@RequestMapping(path = "/admin/location/types")
@RequiredArgsConstructor
@Validated
public class AdminLocationTypeController {
    private final PrintLogs printLogs;
    private final LocationService locationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationTypeDto adminAddLocationType(@RequestBody @Valid NewLocationTypeDto newLocationTypeDto,
                                                HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Attempt to save location type with name {}", newLocationTypeDto.getName());

        return locationService.adminAddLocationType(newLocationTypeDto);
    }

    @PatchMapping(path = "/{id}")
    public LocationTypeDto adminUpdateLocationType(@RequestBody @Valid NewLocationTypeDto typeDto,
                                                   @PathVariable @Positive long id,
                                                   HttpServletRequest httpServletRequest) {

        printLogs.printUrl(httpServletRequest);
        log.info("Attempt to update the name of the location type with identifier {} to {}", id, typeDto.getName());

        return locationService.adminUpdateLocationType(id, typeDto);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminRemoveLocationType(@PathVariable @Positive long id, HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Attempt to delete location type  with identifier {}", id);

        locationService.adminRemoveLocationType(id);
    }
}
