package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;

import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    @Override
    public EventFullDto initiatorAddEvent(long userId, NewEventDto newEventDto) {
        User initiator = userService.findUserById(userId);
        Category category = categoryService.findCategoryById(newEventDto.getCategory());
        Event event = eventMapper.toModel(newEventDto, category, initiator);

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }
}
