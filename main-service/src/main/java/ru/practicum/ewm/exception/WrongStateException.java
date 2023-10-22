package ru.practicum.ewm.exception;

public class WrongStateException extends RuntimeException{

    public WrongStateException(String message) {
        super(message);
    }
}
