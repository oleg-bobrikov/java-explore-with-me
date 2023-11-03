package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

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
