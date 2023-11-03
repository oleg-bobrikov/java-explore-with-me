package ru.practicum.ewm.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class NewLocationDto {
    @Min(-90)
    @Max(90)
    @NotNull
    private Float lat;

    @Min(-180)
    @Max(180)
    @NotNull
    private Float lon;
    private Long type;
    private int radiusInMeters;
}
