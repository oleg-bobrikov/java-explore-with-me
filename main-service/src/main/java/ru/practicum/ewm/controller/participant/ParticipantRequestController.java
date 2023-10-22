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
public class ParticipantRequestController {
    private final ParticipationRequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto participantAddRequest(@PathVariable long userId, @RequestParam long eventId) {
        log.info("User with id = {} is adding a request for event with id = {}", userId, eventId);
        return service.addRequest(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> participantGetRequests(@PathVariable long userId) {
        log.info("User with id = {} is getting requests", userId);
        return service.getRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto participantCancelRequest(@PathVariable long userId, @PathVariable long requestId) {
        log.info("User with id = {} is canceling request with id = {}", userId, requestId);
        return service.cancelRequest(userId, requestId);
    }
}
