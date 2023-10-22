package ru.practicum.ewm.model;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.ParticipantRequestValidationException;
import ru.practicum.ewm.repository.ParticipationRequestRepository;

import javax.persistence.PostPersist;

@Service
public class ParticipationRequestListener {
    private ParticipationRequestRepository requestRepository;

    @PostPersist
    public void postPersist(Object entity) {
        if (!(entity instanceof ParticipationRequest)) {
            return;
        }

        ParticipationRequest request = (ParticipationRequest) entity;
        int participantLimit = request.getEvent().getParticipantLimit();
        if (participantLimit == 0 || request.getStatus().equals(EventState.CANCELED)) {
            return;
        }

        if (participantLimit < requestRepository.countParticipantsByEvent(request.getEvent())) {
            throw new ParticipantRequestValidationException("Participant limit exceeded.");
        }
    }
}