package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    @Builder.Default
    private Set<Long> events = new HashSet<>();

    private boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}
