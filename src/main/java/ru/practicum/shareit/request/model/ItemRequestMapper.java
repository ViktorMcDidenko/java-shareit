package ru.practicum.shareit.request.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    private final ItemMapper itemMapper;

    public ItemRequest toItemRequest(ItemRequestDto dto, User requestor) {
        return new ItemRequest(dto.getDescription(), requestor);
    }

    public ItemRequestDto toDto(ItemRequest request) {
        return new ItemRequestDto(request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemMapper.toDtoList(request.getItems()));
    }

    public List<ItemRequestDto> toList(List<ItemRequest> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }
}