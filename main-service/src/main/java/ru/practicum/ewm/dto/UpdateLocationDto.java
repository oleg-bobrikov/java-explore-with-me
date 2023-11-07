package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdateLocationDto {
    @Min(-90)
    @Max(90)
    private Float lat;

    @Min(-180)
    @Max(180)
    private Float lon;

    private Long type;
    private Integer radiusInMeters;
}
