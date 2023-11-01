package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdateParticipationRequestByInitiatorDto {
    @NotNull
    private List<Long> requestIds;

    @NotNull
    private Status status;

    public enum Status {
        CONFIRMED,
        REJECTED
    }
}
