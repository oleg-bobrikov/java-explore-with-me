package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.validator.LocalDateTimeIsAfter2Hours;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.User;

import javax.validation.constraints.*;
import javax.validation.Valid;
import java.time.LocalDateTime;

import static ru.practicum.ewm.common.Constant.DATE_TIME_PATTERN;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDto {
    private String annotation;
    private Category category;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private User initiator;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration = true;
    private String title;
}
