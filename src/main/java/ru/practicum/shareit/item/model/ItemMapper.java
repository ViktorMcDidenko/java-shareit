package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public Item toItem(ItemDto dto) {
        return new Item(
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable()
        );
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public List<ItemDto> toDtoList(List<Item> l) {
        return l.stream().map(this::toItemDto).collect(Collectors.toList());
    }
}