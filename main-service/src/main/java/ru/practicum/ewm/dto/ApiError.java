package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.common.Constant.DATE_TIME_PATTERN;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private List<String> errors;

    private String message;

    private String reason;

    private HttpStatus status;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    String path;

    @JsonProperty("class")
    String errorClass;
}