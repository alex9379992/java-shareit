package ru.yandex.practicum.shareIt.exeptions;

public class PaginationException extends RuntimeException{
    public PaginationException(String message) {
        super(message);
    }
}
