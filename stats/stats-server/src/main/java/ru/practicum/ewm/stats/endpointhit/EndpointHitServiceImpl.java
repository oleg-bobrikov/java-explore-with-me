package ru.practicum.ewm.stats.endpointhit;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.EndpointHitResponseDto;
import ru.practicum.ewm.stats.dto.EndpointHitRequestDto;
import ru.practicum.ewm.stats.dto.ViewStatsResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;
    private final EndPointHitMapper endPointHitMapper;

    @Override
    public EndpointHitResponseDto saveHit(EndpointHitRequestDto requestDto) {
        return endPointHitMapper.toDto(endpointHitRepository.save(endPointHitMapper.toEntity(requestDto)));
    }

    @Override
    public List<ViewStatsResponseDto> getStatistics(LocalDateTime startDate, LocalDateTime endDate, List<String> uris, boolean unique) {
        return endpointHitRepository.getStats(startDate, endDate, uris, unique);
    }
}
