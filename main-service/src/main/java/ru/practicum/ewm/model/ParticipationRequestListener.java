package ru.practicum.ewm.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.ParticipantRequestValidationException;
import ru.practicum.ewm.repository.ParticipationRequestRepository;

import javax.persistence.PostPersist;

@Service
@RequiredArgsConstructor
public class ParticipationRequestListener {
    private ParticipationRequestRepository requestRepository;

    public ParticipationRequestListener(ParticipationRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @PostPersist
    public void postPersist(Object entity) {
        if (!(entity instanceof ParticipationRequest)) {
            return;
        }

        ParticipationRequest request = (ParticipationRequest) entity;
        int participantLimit = request.getEvent().getParticipantLimit();
        if (participantLimit == 0 || !request.getStatus().equals(Event.State.PENDING)) {
            return;
        }

        if (participantLimit < requestRepository.countParticipantsByEvent(request.getEvent())) {
            throw new ParticipantRequestValidationException("Participant limit exceeded.");
        }
    }
}
