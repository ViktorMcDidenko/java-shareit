package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public Item toItem(User owner, ItemDto dto) {
        return new Item(dto.getId(),
                owner,
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                dto.getRequestId()
        );
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }

    public ItemDto toItemDto(Item item, List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                comments
        );
    }

    public List<ItemDto> toDtoList(List<Item> l) {
        return l.stream().map(this::toItemDto).collect(Collectors.toList());
    }

    public List<ItemDto> toDtoList(List<Item> items, List<Comment> comments) {
        Map<Long, ItemDto> map = items.stream().collect(Collectors.toMap(Item::getId, this::toItemDto, (a, b) -> b));
        comments.forEach(c -> map.get(c.getItem().getId()).getComments().add(new CommentMapper().toDto(c)));
        return new ArrayList<>(map.values());
    }

    public ItemDto toOwnerItemDto(Item item, BookingItemDto nextBooking, BookingItemDto lastBooking,
                                  List<CommentDto> comments) {
        ItemDto dto = toItemDto(item);
        dto.setNextBooking(nextBooking);
        dto.setLastBooking(lastBooking);
        dto.setComments(comments);
        return dto;
    }
}