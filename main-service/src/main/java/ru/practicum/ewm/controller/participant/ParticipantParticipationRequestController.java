package ru.practicum.ewm.controller.participant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.ParticipationRequestService;
import ru.practicum.ewm.util.PrintLogs;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class ParticipantParticipationRequestController {
    private final ParticipationRequestService service;
    private final PrintLogs printLogs;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto participantAddRequest(@PathVariable @Positive long userId,
                                                         @RequestParam @Positive long eventId,
                                                         HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("User ID: {} is adding a request to participate in event ID: {}", userId, eventId);
        return service.addRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> participantGetRequests(@PathVariable @Positive long userId,
                                                                HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("User ID {} is getting requests", userId);
        return service.getRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto participantCancelRequest(@PathVariable @Positive long userId,
                                                            @PathVariable @Positive long requestId,
                                                            HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("User ID: {} is canceling request ID: {}", userId, requestId);
        return service.cancelRequest(userId, requestId);
    }
}
