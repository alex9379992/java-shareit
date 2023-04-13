package ru.yandex.practicum.shareIt.exeptions;

public class ValidationException extends IllegalArgumentException{
    public ValidationException(String message) {
        super(message);
    }
}
