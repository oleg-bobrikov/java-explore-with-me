    package ru.practicum.ewm.controller.participant;

    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.HttpStatus;
    import org.springframework.web.bind.annotation.*;
    import ru.practicum.ewm.dto.ParticipationRequestDto;
    import ru.practicum.ewm.service.ParticipationRequestService;

    import java.util.List;

    @RestController
    @Slf4j
    @RequestMapping(path = "/users/{userId}/requests")
    @RequiredArgsConstructor
    public class ParticipantRequestController {
        private final ParticipationRequestService service;
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public ParticipationRequestDto participantAddRequest(@PathVariable long userId, @RequestParam long eventId) {
            return service.participantAddRequest(userId, eventId);
        }

        @GetMapping
        @ResponseStatus(HttpStatus.OK)
        public List<ParticipationRequestDto> participantGetRequests(@PathVariable long userId){
            return  service.participantGetRequests(userId);
        }

    }
