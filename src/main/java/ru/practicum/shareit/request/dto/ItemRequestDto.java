package ru.practicum.shareit.request.dto;

import lombok.Value;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Value
public class ItemRequestDto {
    long id;
    @NotBlank
    String description;
    LocalDateTime created;
    List<ItemDto> items;
}