package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationDto {
    Set<Integer> events;
    Boolean pinned;

    @Size(min = 1, max = 50)
    String title;
}
