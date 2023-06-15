package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        User user = UserMapper.toUser(userService.getById(userId));
        Item item = itemMapper.toItem(user, itemDto);
        item.setOwner(user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto, long id) {
        Item savedItem = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("There is no item with id %d.", id)));
        if (savedItem.getOwner().getId() != userId) {
            throw new NoAccessException("You do not have rights to edit this item.");
        }
        if (itemDto.getName() != null) {
            savedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            savedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            savedItem.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(itemRepository.save(savedItem));
    }

    @Override
    public ItemDto getById(long userId, long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("There is no item with id %d.", id)));
        if (item.getOwner().getId() == userId) {
            LocalDateTime now = LocalDateTime.now();
            Optional<Booking> lastBooking = bookingRepository.getLastBooking(id, now);
            BookingItemDto lb = null;
            if (lastBooking.isPresent()) {
                lb = bookingMapper.toBookingItemDto(lastBooking.get());
            }
            Optional<Booking> nextBooking = bookingRepository.getNextBooking(id, now);
            BookingItemDto nb = null;
            if (nextBooking.isPresent()) {
                nb = bookingMapper.toBookingItemDto(nextBooking.get());
            }
            return itemMapper.toOwnerItemDto(item, nb, lb);
        }
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> get(long userId) {
        List<Item> items = itemRepository.findByOwnerIdIs(userId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        LocalDateTime now = LocalDateTime.now();
        Set<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toSet());
        List<Booking> lastBooking = bookingRepository
                .findByItemIdInAndEndIsBefore(itemsId, now, Sort.by(Sort.Direction.ASC, "end"));
        List<Booking> nextBooking = bookingRepository.findByItemIdInAndEndIsAfterOrderByStartDesc(itemsId, now);
        if (lastBooking.isEmpty() && nextBooking.isEmpty()) {
            return itemMapper.toDtoList(items);
        }
        Map<ItemDto, BookingItemDto> last = lastBooking.stream().collect(Collectors
                .toMap(booking -> itemMapper.toItemDto(booking.getItem()), bookingMapper::toBookingItemDto, (a, b) -> b));
        Map<ItemDto, BookingItemDto> next = nextBooking.stream().collect(Collectors
                .toMap(booking -> itemMapper.toItemDto(booking.getItem()), bookingMapper::toBookingItemDto, (a, b) -> b));
        List<ItemDto> itemDtos = itemMapper.toDtoList(items);
        itemDtos.forEach(item -> {
            item.setLastBooking(last.get(item));
            item.setNextBooking(next.get(item));
        });
        itemDtos.sort((o1, o2) -> {
            if (o1.getId().equals(o2.getId()))
                return 0;
            return o1.getId() < o2.getId() ? -1 : 1;
        });
        return itemDtos;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemMapper.toDtoList(itemRepository.search(text));
    }
}