package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.common.Constant.*;

@RestController
@Slf4j
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                         @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                         @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size,
                                         HttpServletRequest httpServletRequest) {

        log.info("{}: {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        log.info("Attempt to get compilations:");

        if (pinned != null) {
            log.info("pinned: {}", pinned);
        }
        log.info("from: {}", from);
        log.info("size: {}", size);
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);

        return compilationService.getCompilations(pinned, page);
    }

    @GetMapping(path = "/{compId}")
    CompilationDto getCompilation(@PathVariable long compId, HttpServletRequest httpServletRequest) {

        log.info("{}: {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        log.info("Attempt to get compilation by ID: {}", compId);

        return compilationService.getCompilation(compId);
    }
}
