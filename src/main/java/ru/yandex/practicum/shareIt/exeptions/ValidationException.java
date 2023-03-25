package ru.yandex.practicum.shareIt.exeptions;

public class ValidationException extends RuntimeException{
    public ValidationException(String message) {
        super(message);
    }
}
