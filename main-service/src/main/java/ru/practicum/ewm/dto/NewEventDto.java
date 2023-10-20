package ru.practicum.ewm.dto;

import javax.validation.constraints.NotNull;

public class NewEventDto {
    @NotNull
    private Boolean requestModeration = true;
}
