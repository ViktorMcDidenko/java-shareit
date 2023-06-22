package ru.practicum.shareit.booking.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {
    public BookingDto toDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booking.getBooker(),
                booking.getItem());
    }

    public Booking toBooking(BookingDtoCreate bookingDto, Item item, User booker) {
        return new Booking(bookingDto.getStart(), bookingDto.getEnd(), item, booker, Status.WAITING);
    }

    public List<BookingDto> toList(List<Booking> l) {
        return l.stream().map(this::toDto).collect(Collectors.toList());
    }

    public BookingItemDto toBookingItemDto(Booking booking) {
        return new BookingItemDto(booking.getId(), booking.getBooker().getId());
    }
}