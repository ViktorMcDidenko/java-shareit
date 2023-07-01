package ru.practicum.shareit.exception;

public class NoAccessException extends RuntimeException {
    public NoAccessException(final String message) {
        super(message);
    }
}