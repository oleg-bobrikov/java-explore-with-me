package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.ewm.util.PageRequestHelper;

import java.util.*;
import java.util.stream.Collectors;

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
        List<Event> events = findAllEventsById(newCompilationDto.getEvents());
        Compilation savedCompilation = compilationRepository.save(compilationMapper.toModel(newCompilationDto, events));

        return mapToDto(savedCompilation);
    }

    @Override
    public void adminRemoveCompilation(long compId) {
        Compilation compilation = compilationRepository.findCompilationById(compId);
        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto adminUpdateCompilation(long compId, UpdateCompilationDto patch) {
        Compilation originalCompilation = compilationRepository.findCompilationById(compId);
        Compilation updatedCompilation = applyPatch(originalCompilation, patch);
        Compilation savedCompilation = compilationRepository.save(updatedCompilation);

        return mapToDto(savedCompilation);
    }

    private Compilation applyPatch(Compilation compilation, UpdateCompilationDto patch) {
        Compilation.CompilationBuilder compilationBuilder = compilation.toBuilder();

        if (patch.getTitle() != null) {
            compilationBuilder.title(patch.getTitle());
        }

        if (patch.getPinned() != null) {
            compilationBuilder.pinned(patch.getPinned());
        }

        if (patch.getEvents() != null) {
            List<Event> events = findAllEventsById(patch.getEvents());
            compilationBuilder.events(new HashSet<>(events));
        }
        return compilationBuilder.build();
    }

    private List<Event> findAllEventsById(Iterable<Long> ids) {
        List<Event> events = eventRepository.findAllById(ids);
        Set<Long> foundIds = events.stream().map(Event::getId).collect(Collectors.toSet());
        if (!ids.equals(foundIds)) {
            throw new NotFoundException("Not all events found");
        }
        return events;
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest page = PageRequestHelper.of(from, size);
        List<Compilation> compilations = pinned == null
                ? compilationRepository.findAll(page).getContent()
                : compilationRepository.findAllByPinnedIs(pinned, page);

        return compilations.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(long compId) {
        return mapToDto(compilationRepository.findCompilationById(compId));
    }

    private CompilationDto mapToDto(Compilation compilation) {
        return compilationMapper.toDto(compilation, eventService.mapToEventShortDto(new ArrayList<>(compilation.getEvents())));
    }
}
