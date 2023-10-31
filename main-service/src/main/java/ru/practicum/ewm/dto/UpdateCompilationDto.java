package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationDto {
    Set<Long> events;
    Boolean pinned;

    @NotEmpty
    @Size(min = 1, max = 50)
    String title;
}
