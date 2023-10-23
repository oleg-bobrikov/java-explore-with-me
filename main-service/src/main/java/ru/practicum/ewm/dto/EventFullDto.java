package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.Event;

import java.time.LocalDateTime;

import static ru.practicum.ewm.common.Constant.DATE_TIME_PATTERN;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    private long id;
    private UserShortDto initiator;
    private LocationDto location;
    private boolean paid;
    private int participantLimit;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime publishedOn;

    private boolean requestModeration = true;
    private Event.State state;
    private String title;
    private long views;
}
