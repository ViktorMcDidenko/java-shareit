package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;

    @Override
    public BookingDto add(long bookerId, BookingDtoCreate bookingDtoCreate) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException(String.format("There is no user with id %d.", bookerId)));
        Item item = itemRepository.findById(bookingDtoCreate.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("There is no item with id %d.",
                        bookingDtoCreate.getItemId())));
        if (bookerId == item.getOwner().getId()) {
            throw new NotFoundException("You can not book your own item.");
        }
        if (!item.getAvailable()) {
            throw new RuntimeException(String.format("The item %d is unavailable.", item.getId()));
        }
        if (!bookingDtoCreate.getEnd().isAfter(bookingDtoCreate.getStart())) {
            throw new RuntimeException(String.format("The item %d is unavailable for these dates.", item.getId()));
        }
        Booking booking = bookingRepository.save(mapper.toBooking(bookingDtoCreate, item, booker));
        return mapper.toDto(booking);
    }

    @Override
    public BookingDto approve(long ownerId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("There is no booking with id %d.", bookingId)));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new NotFoundException("You do not have rights to edit this booking.");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new RuntimeException("You have already approved this booking.");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return mapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("There is no booking with id %d.", bookingId)));
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new NotFoundException("You have no bookings with id " + bookingId);
        }
        return mapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllBooker(long bookerId, String state, int from, int size) {
        State s = State.set(state);
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException(String.format("There is no user with id %d.", bookerId));
        }
        LocalDateTime currentDate = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        switch (s) {
            case ALL:
                return mapper.toList(bookingRepository.findByBookerIdOrderByStartDesc(bookerId, pageable));
            case CURRENT:
                return mapper.toList(bookingRepository
                        .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(bookerId, currentDate,
                                currentDate, pageable));
            case PAST:
                return mapper.toList(bookingRepository
                        .findByBookerIdAndEndIsBeforeOrderByEndDesc(bookerId, currentDate, pageable));
            case FUTURE:
                return mapper.toList(bookingRepository
                        .findByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, currentDate, pageable));
            case WAITING:
                return mapper.toList(bookingRepository
                        .findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING, pageable));
            case REJECTED:
                return mapper.toList(bookingRepository
                        .findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED, pageable));
            default:
                throw new RuntimeException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDto> getAllOwner(long ownerId, String state, int from, int size) {
        State s = State.set(state);
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("There is no user with id %d.", ownerId)));
        LocalDateTime currentDate = LocalDateTime.now();
        Set<Long> items = itemRepository.findByOwnerIdIs(ownerId).stream().map(Item::getId).collect(Collectors.toSet());
        if (items.isEmpty()) {
            return null;
        }
        Pageable pageable = PageRequest.of(from / size, size);
        switch (s) {
            case ALL:
                return mapper.toList(bookingRepository.findAllByItemIdInOrderByStartDesc(items, pageable));
            case CURRENT:
                return mapper.toList(bookingRepository
                        .findByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(items, currentDate,
                                currentDate, pageable));
            case PAST:
                return mapper.toList(bookingRepository
                        .findByItemIdInAndEndIsBeforeOrderByEndDesc(items, currentDate, pageable));
            case FUTURE:
                return mapper.toList(bookingRepository.findByItemIdInAndStartIsAfterOrderByStartDesc(items, currentDate,
                        pageable));
            case WAITING:
                return mapper.toList(bookingRepository.findByItemIdInAndStatus(items, Status.WAITING, pageable));
            case REJECTED:
                return mapper.toList(bookingRepository.findByItemIdInAndStatus(items, Status.REJECTED, pageable));
            default:
                throw new RuntimeException("Unknown state: " + state);
        }
    }
}