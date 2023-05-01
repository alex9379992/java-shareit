package ru.yandex.practicum.shareIt.exeptions;

public class RequestNotFoundException extends RuntimeException{
    public RequestNotFoundException(String message) {
        super(message);
    }
}
