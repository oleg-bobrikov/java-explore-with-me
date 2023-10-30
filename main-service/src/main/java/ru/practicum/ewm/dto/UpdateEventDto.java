package ru.practicum.ewm.dto;

import java.time.LocalDateTime;

public interface UpdateEventDto {
    String getAnnotation();
    Long getCategory();
    String getDescription();
    LocalDateTime getEventDate();
    LocationDto getLocation();
    Boolean getPaid();
    Integer getParticipantLimit();
    Boolean getRequestModeration();
    String getTitle();
}
