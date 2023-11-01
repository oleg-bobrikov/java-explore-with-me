package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto adminAddCompilation(NewCompilationDto newCompilationDto);

    void adminRemoveCompilation(long compId);

    CompilationDto adminUpdateCompilation(long compId, UpdateCompilationDto updateCompilationDto);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilation(long compId);
}
