package ru.practicum.ewm.exception;

public class ParticipantRequestValidationException extends RuntimeException{
    public ParticipantRequestValidationException(String message) {
        super(message);
    }
}
