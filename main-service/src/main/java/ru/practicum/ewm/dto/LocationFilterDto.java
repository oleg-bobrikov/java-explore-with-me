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
    private float latMin;
    private float latMax;
    private float lonMin;
    private float lonMax;
    private int from;
    private int size;
    private Set<Long> types;
}
