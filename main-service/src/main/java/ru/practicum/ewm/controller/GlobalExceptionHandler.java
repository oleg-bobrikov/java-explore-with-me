package ru.practicum.ewm.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.ewm.dto.ApiError;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Collections;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handle(Throwable exception) {
        ApiError apiError = getApiError(exception, HttpStatus.INTERNAL_SERVER_ERROR);
        log.debug("{} exception {}", exception.getClass(), exception.getMessage(), exception);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException exception) {
        ApiError apiError = getApiError(exception, HttpStatus.NOT_FOUND);
        log.debug("NotFoundException {}", exception.getMessage(), exception);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @Override
    protected @NonNull ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                           @NonNull HttpHeaders headers,
                                                                           @NonNull HttpStatus status,
                                                                           @NonNull WebRequest request) {
        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }

    private ApiError getApiError(Throwable e, HttpStatus httpStatus) {
        return ApiError.builder()
                .errors(Collections.singletonList(e.getMessage()))
                .message(e.getLocalizedMessage())
                .reason(getReason(e, httpStatus))
                .status(httpStatus)
                .build();
    }

    private String getReason(Throwable e, HttpStatus httpStatus) {
        switch (httpStatus) {
            case BAD_REQUEST:
                return "Incorrectly made request.";
            case NOT_FOUND:
                return "The required object was not found.";
            case CONFLICT:
                return "Integrity constraint has been violated.";
            default:
                return String.valueOf(e.getCause());
        }
    }

}
