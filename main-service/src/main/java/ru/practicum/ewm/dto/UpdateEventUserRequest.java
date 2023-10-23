package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.dto.validator.LocalDateTimeIsAfter2Hours;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

import static ru.practicum.ewm.common.Constant.DATE_TIME_PATTERN;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdateEventUserRequest {
    @Length(min = 20, max = 7000, message = "annotation must be at least 20 and maximum 7000 characters long")
    private String annotation;

    @Positive
    private Long category;

    @Length(min = 20, max = 7000, message = "description must be at least 20 and maximum 7000 characters long")
    private String description;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @LocalDateTimeIsAfter2Hours
    private LocalDateTime eventDate;

    @Valid
    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;
    private StateAction stateAction;

    @Length(min = 3, max = 120, message = "title must be at least 3 and maximum 120 characters long")
    private String title;

    public enum StateAction {
        SEND_TO_REVIEW,
        CANCEL_REVIEW
    }
}
