package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.projection.EventShortProjection;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<EventShortProjection> findByInitiatorId(long userId, Pageable page);

    default Event findEventById(long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format("No event found with identifier %s", id)));
    }
}
