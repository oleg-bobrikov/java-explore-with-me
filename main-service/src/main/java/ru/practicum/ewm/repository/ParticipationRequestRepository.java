package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;

import java.util.*;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    @Query("select " +
            "   count(request)  as requests " +
            "from " +
            "   ParticipationRequest request " +
            "where " +
            "   request.event = :event and request.status = 'CONFIRMED' ")
    int confirmedParticipantsByEvent(@Param("event") Event event);

    List<ParticipationRequest> findAllByRequester(User requester);

    Optional<ParticipationRequest> findByIdAndRequester(long id, User requester);

    default ParticipationRequest findRequestByIdAndRequester(long id, User requester) {
        return findByIdAndRequester(id, requester).orElseThrow(
                () -> new NotFoundException(
                        String.format("No participation request found with id %s and requester_id %s", id, requester.getId())));
    }

    default ParticipationRequest findRequestById(long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format("No participation request found with id %s", id)));
    }


    List<ParticipationRequest> findAllByEventIdAndEventInitiatorId(long eventId, long initiatorId);

    List<ParticipationRequest> findAllByStatusAndEventIdIn(ParticipationRequest.Status status, Collection<Long> eventIds);
}
