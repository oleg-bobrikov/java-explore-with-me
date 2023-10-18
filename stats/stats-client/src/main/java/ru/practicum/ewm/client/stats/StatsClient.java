package ru.practicum.ewm.client.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.dto.EndpointHitResponseDto;
import ru.practicum.ewm.stats.dto.EndpointHitRequestDto;
import ru.practicum.ewm.stats.dto.ViewStatsResponseDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsClient {
    private final RestTemplate restTemplate;

    @Value("${stats-server.url}")
    private final String serverUrl;

    public ResponseEntity<EndpointHitResponseDto> createHit(EndpointHitRequestDto endpointHitRequestDto) {
        return restTemplate.postForEntity(serverUrl.concat("/hit"), endpointHitRequestDto, EndpointHitResponseDto.class);
    }

    public ResponseEntity<ViewStatsResponseDto> getStats(LocalDateTime startDate, LocalDateTime endDate,
                                               List<String> uris, boolean unique) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "start", startDate,
                "end", endDate,
                "unique", unique,
                "uris", uris));

        return restTemplate.getForEntity(
                serverUrl.concat("/stats?start={start}&end={end}&uris={uris}&unique={unique}"),
                ViewStatsResponseDto.class, parameters);
    }

}
