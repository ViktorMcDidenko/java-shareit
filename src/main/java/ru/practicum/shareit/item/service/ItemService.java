package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(long userId, ItemDto item);

    ItemDto update(long userId, ItemDto item, long id);

    ItemDto getById(long userId, long id);

    List<ItemDto> get(long userId);

    List<ItemDto> search(String text);
}