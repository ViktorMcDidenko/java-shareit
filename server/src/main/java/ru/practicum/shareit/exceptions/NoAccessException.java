package ru.practicum.shareit.exceptions;

public class NoAccessException extends RuntimeException {
    public NoAccessException(final String message) {
        super(message);
    }
}