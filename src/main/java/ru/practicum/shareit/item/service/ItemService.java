package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(long userId, ItemDto item);

    ItemDto update(long userId, ItemDto item, long id);

    ItemDto getById(long userId, long id);

    List<ItemDto> get(long userId, Pageable pageable);

    List<ItemDto> search(String text, Pageable pageable);

    CommentDto addComment(long userId, CommentDto commentDto, long itemId);
}