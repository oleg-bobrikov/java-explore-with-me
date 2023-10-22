package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.service.EventService;

@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;
}
