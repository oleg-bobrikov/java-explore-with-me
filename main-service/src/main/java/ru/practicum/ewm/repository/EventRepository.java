package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Event;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByInitiatorId(long userId, Pageable page);

    Optional<Event> findByInitiatorIdAndId(long userId, long id);

    @Query("select e from Event e " +
            "where (coalesce(:userIds, null) is null or e.initiator.id in :userIds) " +
            "and (coalesce(:states, null) is null or e.state in :states) " +
            "and (coalesce(:categoryIds, null) is null or e.category.id in :categoryIds) " +
            "and (coalesce(:rangeStart, null) is null or e.eventDate >= :rangeStart) " +
            "and (coalesce(:rangeEnd, null) is null or e.eventDate <= :rangeEnd) ")
    List<Event> findByAdmin(@Param("userIds") Collection<Long> userIds,
                            @Param("states") Collection<Event.State> states,
                            @Param("categoryIds") Collection<Long> categoryIds,
                            @Param("rangeStart") LocalDateTime rangeStart,
                            @Param("rangeEnd") LocalDateTime rangeEnd,
                            Pageable pageable);

    default Event findEventById(long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format("No event found with identifier %s", id)));
    }


    @Query("select e from Event e where e.id = :eventId and e.state = 'PUBLISHED'")
    Optional<Event> findPublishedEventById(@Param("eventId") long eventId);

    @Query("select e " +
            "from Event e " +
            "where " +
            "   e.state = 'PUBLISHED' " +
            "   and (coalesce(:text, null) is null or (lower(e.annotation) like lower(concat('%', :text, '%')) or lower(e.description) like lower(concat('%', :text, '%')))) " +
            "   and (coalesce(:categoryIds, null) is null or e.category.id in :categoryIds) " +
            "   and (coalesce(:paid, null) is null or e.paid = :paid) " +
            "   and e.eventDate >= :rangeStart " +
            "   and (coalesce(:rangeEnd, null) is null or e.eventDate <= :rangeEnd) " +
            "   and (:onlyAvailable = false or e in " +
            "   (select " +
            "       pr.event as event " +
            "   from ParticipationRequest pr " +
            "   where pr.status = 'CONFIRMED' " +
            "   group by pr.event " +
            "   having min(pr.event.participantLimit) - count(pr.id) > 0 )" +
            ")")
    List<Event> findAllByFilter(@Param("text") String text,
                                               @Param("categoryIds") Set<Long> categoryIds,
                                               @Param("paid") Boolean paid,
                                               @Param("rangeStart") LocalDateTime rangeStart,
                                               @Param("rangeEnd") LocalDateTime rangeEnd,
                                               @Param("onlyAvailable") boolean onlyAvailable,
                                               Pageable page);

    @Query("SELECT MIN(e.publishedOn) FROM Event e WHERE e.id IN :eventIds and e.state ='PUBLISHED' ")
    Optional<LocalDateTime> getFirstPublicationDate(@Param("eventIds") Set<Long> eventIds);
}
