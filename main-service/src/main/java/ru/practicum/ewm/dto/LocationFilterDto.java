package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationFilterDto {
    float latMin;
    float latMax;
    float lonMin;
    float lonMax;
    int from;
    int size;
    Set<Long> types;
}
