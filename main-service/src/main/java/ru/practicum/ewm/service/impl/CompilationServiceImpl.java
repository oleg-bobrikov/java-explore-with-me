package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.service.EventService;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final EventService eventService;

    @Override
    public CompilationDto adminAddCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();
        newCompilationDto.getEvents().forEach(eventId -> events.add(eventRepository.findEventById(eventId)));

        Compilation savedCompilation = compilationRepository.save(compilationMapper.toModel(newCompilationDto, events));

        return compilationMapper.toDto(savedCompilation, eventService.mapToEventShortDto(new ArrayList<>(savedCompilation.getEvents())));
    }

    @Override
    public void adminRemoveCompilation(long compId) {

    }

    @Override
    public CompilationDto adminUpdateCompilation(long compId, UpdateCompilationDto updateCompilationDto) {
        Compilation originalCompilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(
                        String.format("No event found with identifier %s", compId)));

        return null;
    }

    @Override
    public List<CompilationDto> getCompilations(Map<String, Object> parameters) {
        return null;
    }

    @Override
    public CompilationDto getCompilation(long compId) {
        return null;
    }
}
