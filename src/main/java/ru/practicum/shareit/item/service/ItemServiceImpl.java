package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemStorage repository;
    private final ItemMapper mapper;

    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        userService.getById(userId);
        Item item = mapper.toItem(itemDto);
        item.setOwner(userId);
        return mapper.toItemDto(repository.add(item));
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto, long id) {
        checkOwner(userId, id);
        checkId(id, userId);
        Item item = repository.getById(id);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return mapper.toItemDto(repository.update(item));
    }

    @Override
    public ItemDto getById(Long id) { //если не работает, сделать проверку на id
        return mapper.toItemDto(repository.getById(id));
    }

    @Override
    public List<ItemDto> get(long userId) {
        return mapper.toDtoList(repository.get(userId));
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return mapper.toDtoList(repository.search(text));
    }

    private void checkOwner(long userId, long id) {
        if (repository.getById(id).getOwner() != userId) {
            throw new NoAccessException("You do not have rights to edit this item.");
        }
    }

    private void checkId(long id, long userId) {
        boolean result = get(userId).stream().anyMatch(u -> u.getId() == id);
        if (!result) {
            throw new NotFoundException(String.format("There is no item with id %d.", id));
        }
    }
}