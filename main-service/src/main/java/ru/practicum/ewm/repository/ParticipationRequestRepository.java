package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.dto.ParticipationRequestConfirmation;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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


    @Query("select " +
            "   request.created as created, " +
            "   request.event.id as event, " +
            "   request.id as id, " +
            "   request.status as status " +
            "from " +
            "   ParticipationRequest as request " +
            "where " +
            "   request.event.initiator.id = :initiatorId and request.event.id = :eventId")
    List<ParticipationRequestDto> findAllByInitiatorIdAndEventId(@Param("initiatorId") long initiatorId,
                                                                 @Param("eventId") long eventId);
    @Query("select " +
            "   pr.event.id as id, " +
            "   count(pr.event.id) as confirmedRequests " +
            "from " +
            "   ParticipationRequest pr " +
            "where " +
            "   pr.status = 'CONFIRMED' and pr.event.id in :eventIds and pr.event.state = 'PUBLISHED' " +
            "group by " +
            "   pr.event.id")
    List<ParticipationRequestConfirmation> findAllConfirmedByEventIdIn(@Param("eventIds") Set<Long> eventIds);
}
