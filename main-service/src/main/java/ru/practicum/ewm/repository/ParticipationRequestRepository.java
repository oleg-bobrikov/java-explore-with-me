package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    @Query("select count(event) from Event event where event.state != ru.practicum.ewm.model.EventState.CANCELED")
    int countParticipantsByEvent(Event event);

    List<ParticipationRequest> findAllByRequester(User requester);

    Optional<ParticipationRequest> findByIdAndRequester(long id, User requester);

    default ParticipationRequest findRequestByIdAndRequester(long id, User requester) {
        return findByIdAndRequester(id, requester).orElseThrow(
                () -> new NotFoundException(
                        String.format("No participation request found with id %s and requester_id %s", id, requester.getId())));
    }
}
