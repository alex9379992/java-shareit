package ru.yandex.practicum.shareIt.exeptions;

public class CommentException extends RuntimeException {
    public CommentException(String message) {
        super(message);
    }
}