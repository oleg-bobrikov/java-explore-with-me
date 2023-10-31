package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.util.PrintLogs;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final PrintLogs printLogs;
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto adminAddCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto,
                                              HttpServletRequest httpServletRequest) {

        log.info("{}: {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        printLogs.printObject(newCompilationDto, "Administrator add compilation");
        return compilationService.adminAddCompilation(newCompilationDto);
    }

    @DeleteMapping(path = "/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminRemoveCompilation(@PathVariable long compId, HttpServletRequest httpServletRequest) {

        log.info("{}: {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        log.info("Administrator delete compilation with identifier {}", compId);
        compilationService.adminRemoveCompilation(compId);
    }

    @PatchMapping(path = "/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto adminUpdateCompilation(@RequestBody @Valid UpdateCompilationDto updateCompilationDto,
                                                 @PathVariable long compId,
                                                 HttpServletRequest httpServletRequest) {

        log.info("{}: {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        log.info("Attempt to update compilation with identifier {}", compId);
        printLogs.printObject(updateCompilationDto, "Administrator update compilation");

        return compilationService.adminUpdateCompilation(compId, updateCompilationDto);
    }
}
