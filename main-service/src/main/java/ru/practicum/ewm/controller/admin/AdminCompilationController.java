package ru.practicum.ewm.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final ObjectMapper objectMapper;
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto adminAddCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        try {
            String json = objectMapper.writeValueAsString(newCompilationDto);
            log.info("Administrator add compilation: {}", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getLocalizedMessage());
        }
        return compilationService.adminAddCompilation(newCompilationDto);
    }

    @DeleteMapping(path = "/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminRemoveCompilation(@PathVariable long compId) {
        log.info("Administrator delete compilation with identifier {}", compId);
        compilationService.adminRemoveCompilation(compId);
    }

    @PatchMapping(params = "/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto adminUpdateCompilation(@RequestBody @Valid UpdateCompilationDto updateCompilationDto,
                                                 @PathVariable long compId) {
        try {
            String json = objectMapper.writeValueAsString(updateCompilationDto);
            log.info("Administrator update compilation: {}", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getLocalizedMessage());
        }

        log.info("Attempt to update compilation with identifier {}", compId);
        return compilationService.adminUpdateCompilation(compId, updateCompilationDto);
    }
}
