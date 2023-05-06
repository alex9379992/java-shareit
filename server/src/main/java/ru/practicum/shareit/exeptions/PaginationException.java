package ru.practicum.shareit.exeptions;

public class PaginationException extends RuntimeException{
    public PaginationException(String message) {
        super(message);
    }
}
