package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.model.Event;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventPublicFilterDto {
    String text;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    String uri;
    String ip;
    boolean onlyAvailable;
    Boolean paid;
    Set<Long> categoryIds;
    Event.Sort sort;
    Pageable page;
}
