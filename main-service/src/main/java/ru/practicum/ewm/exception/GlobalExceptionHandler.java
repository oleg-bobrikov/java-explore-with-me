package ru.practicum.ewm.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.dto.ApiError;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Collections;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ApiError handle(Throwable exception, HttpServletRequest httpServletRequest) {
        log.error("An exception occurred while processing the request {}: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                exception);
        return getApiError(exception, httpServletRequest, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            PeriodValidationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(Exception exception, HttpServletRequest httpServletRequest) {
        log.error("An exception occurred while processing the request {}: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                exception);
        return getApiError(exception, httpServletRequest, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({WrongStateException.class, ParticipantRequestException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(RuntimeException exception, HttpServletRequest httpServletRequest) {
        log.error("An exception occurred while processing the request {}: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                exception);
        return getApiError(exception, httpServletRequest, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(NotFoundException exception, HttpServletRequest httpServletRequest) {
        log.error("An exception occurred while processing the request {}: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                exception);
        return getApiError(exception, httpServletRequest, HttpStatus.NOT_FOUND);
    }

    private ApiError getApiError(Throwable e, HttpServletRequest httpServletRequest, HttpStatus httpStatus) {
        return ApiError.builder()
                .errors(Collections.singletonList(e.getMessage()))
                .message(e.getLocalizedMessage())
                .reason(getReason(e, httpStatus))
                .status(httpStatus)
                .path(httpServletRequest.getMethod() + " " + httpServletRequest.getServletPath())
                .errorClass(e.getClass().getName())
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
