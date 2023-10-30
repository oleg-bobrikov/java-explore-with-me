package ru.practicum.ewm.client.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.dto.EndpointHitResponseDto;
import ru.practicum.ewm.stats.dto.EndpointHitRequestDto;
import ru.practicum.ewm.stats.dto.ViewStatsResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.ewm.common.Constant.DATE_TIME_PATTERN;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsClient {
    private final RestTemplate restTemplate;

    @Value("${stats-server.url}")
    private String serverUrl;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public ResponseEntity<EndpointHitResponseDto> createHit(EndpointHitRequestDto endpointHitRequestDto) {
        return restTemplate.postForEntity(serverUrl.concat("/hit"), endpointHitRequestDto, EndpointHitResponseDto.class);
    }

    public List<ViewStatsResponseDto> getStatistics(LocalDateTime startDate, LocalDateTime endDate,
                                                    List<String> uris, boolean unique) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "start", startDate.format(dateTimeFormatter),
                "end", endDate.format(dateTimeFormatter),
                "unique", unique,
                "uris", uris == null ? null : String.join("&", uris)));

        ViewStatsResponseDto[] response = restTemplate.getForObject(
                serverUrl.concat("/stats?start={start}&end={end}&uris={uris}&unique={unique}"),
                ViewStatsResponseDto[].class, parameters);

        return response == null ? List.of() : List.of(response);
    }

}
