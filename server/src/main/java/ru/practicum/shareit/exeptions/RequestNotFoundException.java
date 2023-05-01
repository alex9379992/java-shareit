package ru.practicum.shareit.exeptions;

public class RequestNotFoundException extends RuntimeException{
    public RequestNotFoundException(String message) {
        super(message);
    }
}
