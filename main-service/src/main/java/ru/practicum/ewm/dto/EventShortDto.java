package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.ewm.common.Constant.DATE_TIME_PATTERN;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    private long id;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private long views;
}
