package ru.practicum.ewm.controller.priv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;
    private final ObjectMapper objectMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto initiatorAddEvent(@PathVariable long userId, @RequestBody @Valid NewEventDto newEventDto) {
        try {
            String json = objectMapper.writeValueAsString(newEventDto);
            log.info("Initiator add event: {}", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getLocalizedMessage());
        }

        return eventService.initiatorAddEvent(userId, newEventDto);
    }
}
