package ru.practicum.ewm.stats.endpointhit;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHitAnswerDto;
import ru.practicum.ewm.stats.dto.EndpointHitRequestDto;
import ru.practicum.ewm.stats.dto.ViewStatsResponseDto;


import javax.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static ru.practicum.ewm.stats.common.Constant.DATE_TIME_PATTERN;


@RestController
@RequestMapping()
@AllArgsConstructor
@Slf4j
public class EndPointHitController {

    private final EndpointHitService endpointHitService;

    @RequestMapping(path = "/hit")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)

    public EndpointHitAnswerDto saveHit(@RequestBody @Valid EndpointHitRequestDto requestDto) {
        log.info("Save hit {}", requestDto);
        return endpointHitService.saveHit(requestDto);
    }

    @RequestMapping(path = "/stats")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsResponseDto> getStatistics(@RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime start,
                                                    @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime end,
                                                    @RequestParam(required = false) Set<String> uris,
                                                    @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Get statistics ");
        return endpointHitService.getStatistics(start, end, uris, unique);
    }
}
