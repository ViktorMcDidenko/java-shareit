package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") long bookerId,
                          @Valid @RequestBody BookingDtoCreate bookingDtoCreate) {
        return service.add(bookerId, bookingDtoCreate);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable long bookingId,
                              @RequestParam boolean approved) {
        return service.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return service.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBooker(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        return service.getAllBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        return service.getAllOwner(ownerId, state);
    }
}