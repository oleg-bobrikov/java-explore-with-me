package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdateParticipationRequestByInitiatorResultDto {
    private List<Long> requestIds;
    private Status status;
    public enum Status {
        CONFIRMED,
        REJECTED
    }
}
