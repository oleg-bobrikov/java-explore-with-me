package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.Event;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventPublicFilterDto {
    private String text;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private String uri;
    private String ip;
    private boolean onlyAvailable;
    private Boolean paid;
    private Set<Long> categoryIds;
    private Event.Sort sort;
    private int from;
    private int size;
}
