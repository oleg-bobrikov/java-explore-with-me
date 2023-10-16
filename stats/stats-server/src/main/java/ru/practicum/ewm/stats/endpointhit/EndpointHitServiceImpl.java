package ru.practicum.ewm.stats.endpointhit;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.EndpointHitAnswerDto;
import ru.practicum.ewm.stats.dto.EndpointHitRequestDto;
import ru.practicum.ewm.stats.dto.ViewStatsResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {

    private final EndpointHitRepository endpointHitRepository;
    private final EndPointHitMapper endPointHitMapper;


    @Override
    public EndpointHitAnswerDto saveHit(EndpointHitRequestDto requestDto) {
        return endPointHitMapper.toDto(endpointHitRepository.save(endPointHitMapper.toEntity(requestDto)));
    }

    @Override
    public List<ViewStatsResponseDto> getStatistics(LocalDateTime startDate, LocalDateTime endDate, Set<String> uris, boolean unique) {
        return endpointHitRepository.getStats(startDate, endDate, uris, unique);
    }
}
