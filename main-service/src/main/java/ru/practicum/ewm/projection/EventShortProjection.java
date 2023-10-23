package ru.practicum.ewm.projection;

import org.springframework.data.rest.core.config.Projection;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;
@Projection(
        name = "EventShort",
        types = { Event.class }
)
public interface EventShortProjection {
    String getAnnotation();

    Category getCategory();

    LocalDateTime getEventDate();

    long getId();

    User getInitiator();

    boolean getPaid();

    String getTitle();
}
