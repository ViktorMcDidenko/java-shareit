package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exceptions.ValidationException;

public enum State {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static State set(String state) {
        for (State s : State.values()) {
            if (s.name().equals(state)) {
                return s;
            }
        }
        throw new ValidationException(String.format("There is no %s state", state));
    }
}