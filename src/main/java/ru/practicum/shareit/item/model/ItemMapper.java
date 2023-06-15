package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingItemDto;
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

    public ItemDto toOwnerItemDto(Item item, BookingItemDto nextBooking, BookingItemDto lastBooking) {
        ItemDto dto = toItemDto(item);
        dto.setNextBooking(nextBooking);
        dto.setLastBooking(lastBooking);
        return dto;
    }

    public List<ItemDto> toBookingItemList(Map<ItemDto, BookingItemDto> last, Map<ItemDto, BookingItemDto> next) { //не факт, что это работает
        Set<ItemDto> uniqueItems = new HashSet<>();
        uniqueItems.addAll(last.keySet());
        uniqueItems.addAll(next.keySet());
        uniqueItems.forEach(item -> {
            item.setLastBooking(last.get(item));
            item.setNextBooking(next.get(item));
        });
        return new ArrayList<>(uniqueItems);
    }
}