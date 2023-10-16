package ru.practicum.ewm.stats.dto;

import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


import static ru.practicum.ewm.stats.common.Constant.DATE_TIME_PATTERN;

@Value
public class EndpointHitRequestDto {
    @NotBlank String app;

    @NotBlank String uri;

    @NotBlank String ip;

    @NotNull
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime timestamp;
}