package ru.yandex.practicum.shareIt.exeptions.handlers;

public class BookingNotFoundException extends RuntimeException{
    public BookingNotFoundException(String message) {
        super(message);
    }
}
