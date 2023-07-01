package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.Value;

@Value
@NotNull
public class BookItemRequestDto {
	long itemId;
	@FutureOrPresent
	LocalDateTime start;
	@Future
	LocalDateTime end;
}
