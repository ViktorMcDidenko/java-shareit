package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto add(long requestorId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getYours(long is);

    List<ItemRequestDto> getTheirs(long id, int from, int size);

    ItemRequestDto getById(long userId, long requestId);
}