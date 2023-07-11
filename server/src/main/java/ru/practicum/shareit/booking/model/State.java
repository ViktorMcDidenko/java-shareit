package ru.practicum.shareit.booking.model;

public enum State {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static State set(String state) {
        for (State s : State.values()) {
            if (s.name().equals(state)) {
                return s;
            }
        }
        throw new RuntimeException("Unknown state: " + state);
    }
}