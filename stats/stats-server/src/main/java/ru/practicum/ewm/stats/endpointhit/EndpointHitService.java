package ru.practicum.ewm.stats.endpointhit;

import ru.practicum.ewm.stats.dto.EndpointHitAnswerDto;
import ru.practicum.ewm.stats.dto.EndpointHitRequestDto;
import ru.practicum.ewm.stats.dto.ViewStatsResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EndpointHitService {

    EndpointHitAnswerDto saveHit(EndpointHitRequestDto requestDto);

    List<ViewStatsResponseDto> getStatistics(LocalDateTime startDate, LocalDateTime endDate, Set<String> uris, boolean unique);
}
