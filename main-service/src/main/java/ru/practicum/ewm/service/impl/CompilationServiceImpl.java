package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationDto;
import ru.practicum.ewm.service.CompilationService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    @Override
    public CompilationDto adminAddCompilation(NewCompilationDto newCompilationDto) {
        return null;
    }

    @Override
    public void adminRemoveCompilation(long compId) {

    }

    @Override
    public CompilationDto adminUpdateCompilation(long compId, UpdateCompilationDto updateCompilationDto) {
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
