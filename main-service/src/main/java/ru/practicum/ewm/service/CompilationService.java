package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationDto;

import java.util.List;
import java.util.Map;

public interface CompilationService {
    CompilationDto adminAddCompilation(NewCompilationDto newCompilationDto);

    void adminRemoveCompilation(long compId);

    CompilationDto adminUpdateCompilation(long compId, UpdateCompilationDto updateCompilationDto);

    List<CompilationDto> getCompilations(Boolean pinned, Pageable page);

    CompilationDto getCompilation(long compId);
}
