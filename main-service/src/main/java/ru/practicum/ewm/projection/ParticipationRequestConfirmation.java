package ru.practicum.ewm.projection;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ParticipationRequestConfirmation {
    private Long id;
    private Long confirmedRequests;
}
