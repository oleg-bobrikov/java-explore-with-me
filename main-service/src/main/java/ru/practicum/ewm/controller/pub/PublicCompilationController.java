package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.util.PrintLogs;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.common.Constant.*;

@RestController
@Slf4j
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Validated
public class PublicCompilationController {
    private final CompilationService compilationService;
    private final PrintLogs printLogs;

    @GetMapping
    List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                         @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                         @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size,
                                         HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Attempt to get compilations:");

        if (pinned != null) {
            log.info("pinned: {}", pinned);
        }
        log.info("from: {}", from);
        log.info("size: {}", size);

        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping(path = "/{compId}")
    CompilationDto getCompilation(@PathVariable @Positive long compId, HttpServletRequest httpServletRequest) {
        printLogs.printUrl(httpServletRequest);
        log.info("Attempt to get compilation by ID: {}", compId);

        return compilationService.getCompilation(compId);
    }
}
