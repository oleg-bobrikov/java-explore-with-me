package ru.practicum.ewm.stats.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Value
public class EndpointHitRequestDto {
    @NotBlank String app;
    @NotBlank String uri;
    @NotBlank String ip;

    @LocalDateTimeConstraint
    String timestamp;
}