package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.LocationType;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    private long id;
    private float lat;
    private float lon;
    private LocationType type;
    private int radiusInMeters;
}
