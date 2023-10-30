package ru.practicum.ewm.stats.endpointhit;

import ru.practicum.ewm.stats.dto.EndpointHitResponseDto;
import ru.practicum.ewm.stats.dto.EndpointHitRequestDto;
import ru.practicum.ewm.stats.dto.ViewStatsResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitService {

    EndpointHitResponseDto saveHit(EndpointHitRequestDto requestDto);

    List<ViewStatsResponseDto> getStatistics(LocalDateTime startDate, LocalDateTime endDate, List<String> uris, boolean unique);
}
