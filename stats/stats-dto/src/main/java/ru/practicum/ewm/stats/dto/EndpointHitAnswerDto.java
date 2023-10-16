package ru.practicum.ewm.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static ru.practicum.ewm.common.Constant.DATE_TIME_PATTERN;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitAnswerDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;

    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime timestamp;
}
