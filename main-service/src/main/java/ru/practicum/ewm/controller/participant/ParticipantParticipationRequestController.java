package ru.practicum.ewm.controller.participant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.ParticipationRequestService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class ParticipantParticipationRequestController {
    private final ParticipationRequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto participantAddRequest(@PathVariable long userId, @RequestParam long eventId) {
        log.info("User ID: {} is adding a request for event ID: {}", userId, eventId);
        return service.addRequest(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> participantGetRequests(@PathVariable long userId) {
        log.info("User ID {} is getting requests", userId);
        return service.getRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto participantCancelRequest(@PathVariable long userId, @PathVariable long requestId) {
        log.info("User ID: {} is canceling request ID: {}", userId, requestId);
        return service.cancelRequest(userId, requestId);
    }
}
