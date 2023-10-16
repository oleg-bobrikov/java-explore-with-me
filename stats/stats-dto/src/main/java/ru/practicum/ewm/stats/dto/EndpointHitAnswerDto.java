package ru.practicum.ewm.stats.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static ru.practicum.ewm.stats.common.Constant.DATE_TIME_PATTERN;

@Data
public class EndpointHitAnswerDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;

    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime timestamp;
}
