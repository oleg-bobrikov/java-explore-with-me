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
@RequestMapping(path = "/admin/locations")
@RequiredArgsConstructor
@Validated
public class AdminLocationController {
    private final PrintLogs printLogs;
    private final LocationService locationService;

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
}
