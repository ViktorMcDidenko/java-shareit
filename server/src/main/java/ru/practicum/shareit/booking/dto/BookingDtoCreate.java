package ru.practicum.shareit.booking.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class BookingDtoCreate {
    Long itemId;
    LocalDateTime start;
    LocalDateTime end;
}