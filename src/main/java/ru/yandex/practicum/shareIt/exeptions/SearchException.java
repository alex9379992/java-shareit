package ru.yandex.practicum.shareIt.exeptions;

public class SearchException extends RuntimeException{
    public SearchException(String message) {
        super(message);
    }
}