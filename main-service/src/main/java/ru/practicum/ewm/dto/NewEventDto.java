package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.dto.validator.LocalDateTimeIsAfter2Hours;

import javax.validation.constraints.*;
import javax.validation.Valid;
import java.time.LocalDateTime;

import static ru.practicum.ewm.common.Constant.DATE_TIME_PATTERN;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class NewEventDto {
    @NotBlank
    @Length(min = 20, max = 7000,  message = "annotation must be at least 20 and maximum 7000 characters long")
    private String annotation;

    @Positive
    private long category;

    @NotBlank
    @Length(min = 20, max = 7000,  message = "description must be at least 20 and maximum 7000 characters long")
    private String description;

    @NotNull
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @LocalDateTimeIsAfter2Hours
    private LocalDateTime eventDate;

    @NotNull
    @Valid
    private LocationDto location;

    @Builder.Default
    private boolean paid = false;

    @PositiveOrZero
    @Builder.Default
    private int participantLimit = 0;

    @Builder.Default
    private boolean requestModeration = true;

    @NotBlank
    @Length(min = 3, max = 120,  message = "title must be at least 3 and maximum 120 characters long")
    private String title;
}
