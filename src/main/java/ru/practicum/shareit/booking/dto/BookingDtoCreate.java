package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.Generated;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
@NotNull
@Generated
public class BookingDtoCreate {
    Long itemId;
    @FutureOrPresent
    LocalDateTime start;
    @Future
    LocalDateTime end;
}