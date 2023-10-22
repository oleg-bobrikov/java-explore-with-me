package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.validator.LocalDateTimeIsAfter2Hours;
import ru.practicum.ewm.model.EventState;

import java.time.LocalDateTime;

import static ru.practicum.ewm.common.Constant.DATE_TIME_PATTERN;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @LocalDateTimeIsAfter2Hours
    private LocalDateTime created;

    private long event;
    private long id;
    private long requester;
    private EventState status;
}
