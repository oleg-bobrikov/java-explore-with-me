package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.projection.EventShortProjection;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<EventShortProjection> findByInitiatorId(long userId, Pageable page);

    Optional<EventShortProjection> findByInitiatorIdAndId(long userId, long id);

    default Event findEventById(long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format("No event found with identifier %s", id)));
    }
}
