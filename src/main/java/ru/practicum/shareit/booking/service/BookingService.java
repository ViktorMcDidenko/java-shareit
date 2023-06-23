package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.List;

public interface BookingService {
    BookingDto add(long bookerId, BookingDtoCreate bookingDto);

    BookingDto approve(long ownerId, long bookingId, boolean approved);

    BookingDto getById(long userId, long bookingId);

    List<BookingDto> getAllBooker(long bookerId, String state, int from, int size);

    List<BookingDto> getAllOwner(long ownerId, String state, int from, int size);
}