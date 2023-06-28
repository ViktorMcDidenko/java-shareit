package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingServiceImplTest {
    @Autowired
    BookingService bookingService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    private User owner;
    private User savedOwner;
    private User booker;
    private User savedBooker;
    private Item savedItem;
    private BookingDto result;
    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        owner = new User("владелец", "ya1@ya.ru");
        savedOwner = userRepository.save(owner);
        booker = new User("букер", "ya2@ya.ru"); //id2
        savedBooker = userRepository.save(booker);
        Item item = new Item(owner, "вещь", "description");
        item.setAvailable(true);
        savedItem = itemRepository.save(item);
        BookingDtoCreate dto = new BookingDtoCreate(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)); //id1
        result = bookingService.add(savedBooker.getId(), dto);
    }

    @Test
    @DirtiesContext
    void addGetAndApprove() {
        assertNotNull(result.getId());
        assertEquals(Status.WAITING, result.getStatus());
        assertEquals(savedBooker.getId(), result.getBooker().getId());
        assertEquals(savedItem.getId(), result.getItem().getId());

        List<BookingDto> bookerWaitingResult = bookingService.getAllBooker(savedBooker.getId(),"WAITING",
                PAGEABLE);

        assertEquals(1, bookerWaitingResult.size());
        assertEquals(result.getId(), bookerWaitingResult.get(0).getId());
        assertEquals(Status.WAITING, bookerWaitingResult.get(0).getStatus());

        List<BookingDto> ownerWaitingResult = bookingService.getAllOwner(savedOwner.getId(),"WAITING",
                PAGEABLE);

        assertEquals(1, ownerWaitingResult.size());
        assertEquals(result.getId(), ownerWaitingResult.get(0).getId());
        assertEquals(Status.WAITING, ownerWaitingResult.get(0).getStatus());

        BookingDto approvedResult = bookingService.approve(savedOwner.getId(), result.getId(), true);

        assertEquals(result.getId(), approvedResult.getId());
        assertEquals(Status.APPROVED, approvedResult.getStatus());
    }

    @Test
    @DirtiesContext
    void disapproveAndGet() {
        BookingDto disapprovedResult = bookingService.approve(savedOwner.getId(), result.getId(), false);

        assertEquals(result.getId(), disapprovedResult.getId());
        assertEquals(Status.REJECTED, disapprovedResult.getStatus());

        List<BookingDto> bookerRejectedResult = bookingService.getAllBooker(savedBooker.getId(), "REJECTED",
                PAGEABLE);

        assertEquals(1, bookerRejectedResult.size());
        assertEquals(result.getId(), bookerRejectedResult.get(0).getId());
        assertEquals(Status.REJECTED, bookerRejectedResult.get(0).getStatus());

        List<BookingDto> ownerRejectedResult = bookingService.getAllOwner(savedOwner.getId(), "REJECTED",
                PAGEABLE);

        assertEquals(1, ownerRejectedResult.size());
        assertEquals(result.getId(), ownerRejectedResult.get(0).getId());
        assertEquals(Status.REJECTED, ownerRejectedResult.get(0).getStatus());
    }

    @Test
    void getById() {
        BookingDto foundDto = bookingService.getById(savedBooker.getId(), result.getId());

        assertEquals(result.getId(), foundDto.getId());
    }

    @Test
    @DirtiesContext
    void getAll() {
        BookingDtoCreate pastDto = new BookingDtoCreate(1L,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(2)); //id2
        BookingDtoCreate currentDto = new BookingDtoCreate(1L,
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().plusHours(5)); //id3
        bookingService.add(savedBooker.getId(), pastDto);
        bookingService.add(savedBooker.getId(), currentDto);

        Item falseItem = new Item(booker, "вещьБукера", "не должна попадать в выборку");
        falseItem.setAvailable(true);
        itemRepository.save(falseItem);
        BookingDtoCreate falseDto = new BookingDtoCreate(2L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        bookingService.add(owner.getId(), falseDto);

        List<BookingDto> all = bookingService.getAllBooker(savedBooker.getId(), "ALL", PAGEABLE);

        assertEquals(3, all.size());
        assertEquals(1, all.get(0).getId());
        assertEquals(3, all.get(1).getId());
        assertEquals(2, all.get(2).getId());

        IntStream.range(0, all.size()).forEach(i -> assertEquals(savedBooker.getId(), all.get(i).getBooker().getId()));

        List<BookingDto> current = bookingService.getAllBooker(savedBooker.getId(), "CURRENT", PAGEABLE);

        assertEquals(1, current.size());
        assertEquals(3, current.get(0).getId());

        List<BookingDto> past = bookingService.getAllBooker(savedBooker.getId(), "PAST", PAGEABLE);

        assertEquals(1, past.size());
        assertEquals(2, past.get(0).getId());

        List<BookingDto> future = bookingService.getAllBooker(savedBooker.getId(), "FUTURE", PAGEABLE);

        assertEquals(1, future.size());
        assertEquals(1, future.get(0).getId());

        List<BookingDto> allOwner = bookingService.getAllOwner(savedOwner.getId(), "ALL", PAGEABLE);

        assertEquals(3, allOwner.size());
        assertEquals(1, allOwner.get(0).getId());
        assertEquals(3, allOwner.get(1).getId());
        assertEquals(2, allOwner.get(2).getId());

        IntStream.range(0, allOwner.size()).forEach(i -> assertEquals(savedBooker.getId(),
                allOwner.get(i).getBooker().getId()));

        List<BookingDto> currentOwner = bookingService.getAllOwner(savedOwner.getId(), "CURRENT", PAGEABLE);

        assertEquals(1, currentOwner.size());
        assertEquals(3, currentOwner.get(0).getId());

        List<BookingDto> pastOwner = bookingService.getAllOwner(savedOwner.getId(), "PAST", PAGEABLE);

        assertEquals(1, pastOwner.size());
        assertEquals(2, pastOwner.get(0).getId());

        List<BookingDto> futureOwner = bookingService.getAllOwner(savedOwner.getId(), "FUTURE", PAGEABLE);

        for (BookingDto b : futureOwner) {
            System.out.println(b.getId());
        }

        assertEquals(1, futureOwner.size());
        assertEquals(1, futureOwner.get(0).getId());
    }
}