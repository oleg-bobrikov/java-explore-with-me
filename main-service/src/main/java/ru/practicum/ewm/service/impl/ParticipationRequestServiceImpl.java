package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.exception.ParticipantRequestValidationException;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.ParticipationRequestService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;
    private final ParticipationRequestMapper requestMapper;

    @Override
    @Transactional(rollbackFor = ParticipantRequestValidationException.class)
    public ParticipationRequestDto addRequest(long userId, long eventId) {
        User requester = userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);
        if (requester.equals(event.getInitiator())) {
            throw new ParticipantRequestValidationException("The event initiator cannot add a participation request to their own event.");
        }

        if (!event.getState().equals(Event.State.PUBLISHED)) {
            throw new ParticipantRequestValidationException("Participation in an unpublished event is prohibited.");
        }

        ParticipationRequest.Status status = event.getRequestModeration() ? ParticipationRequest.Status.PENDING : ParticipationRequest.Status.CONFIRMED;
        ParticipationRequest request = ParticipationRequest.builder()
                .requester(requester)
                .event(event)
                .status(status)
                .build();

        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequests(long userId) {
        User requester = userRepository.findUserById(userId);
        return requestMapper.toDto(requestRepository.findAllByRequester(requester));
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        User requester = userRepository.findUserById(userId);
        ParticipationRequest request = requestRepository.findRequestByIdAndRequester(requestId, requester);
        ParticipationRequest canceled = request.toBuilder().status(ParticipationRequest.Status.CANCELED).build();

        return requestMapper.toDto(requestRepository.save(canceled));
    }
}