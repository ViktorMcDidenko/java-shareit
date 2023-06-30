package ru.practicum.shareit.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.Generated;

@RequiredArgsConstructor
@Getter
@Generated
public class ErrorResponse {
    private final String error;
}