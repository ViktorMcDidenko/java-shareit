package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceImplTest {
    @Autowired
    ItemService itemService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookingService bookingService;

    private User savedOwner;
    private User savedBooker;
    private ItemDto itemDto;
    private ItemDto savedItem;
    private BookingDto futureBooking;
    private BookingDto pastBooking;
    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        User owner = new User("owner", "ya@ya.ru"); //id1
        savedOwner = userRepository.save(owner);

        User booker = new User("букер", "a@ya.ru"); //id2
        savedBooker = userRepository.save(booker);

        itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("deScripTion");
        itemDto.setAvailable(true);
        savedItem = itemService.add(savedOwner.getId(), itemDto); //id1

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("item2script");
        itemDto2.setDescription("deion2");
        itemDto2.setAvailable(true);
        itemService.add(savedOwner.getId(), itemDto2); //id2

        ItemDto itemDto3 = new ItemDto();
        itemDto3.setName("item3script");
        itemDto3.setDescription("description3");
        itemDto3.setAvailable(false);
        itemService.add(savedOwner.getId(), itemDto3); //id3

        BookingDtoCreate booking1 = new BookingDtoCreate(savedItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)); //id1
        futureBooking = bookingService.add(savedBooker.getId(), booking1);
        bookingService.approve(savedOwner.getId(), futureBooking.getId(), true);

        BookingDtoCreate booking2 = new BookingDtoCreate(savedItem.getId(),
                LocalDateTime.now().minusDays(15),
                LocalDateTime.now().minusDays(14)); //id2
        pastBooking = bookingService.add(savedBooker.getId(), booking2);
        bookingService.approve(savedOwner.getId(), pastBooking.getId(), true);
    }

    @Test
    @DirtiesContext
    void addAndUpdate() {
        assertNotNull(savedItem);
        assertNotNull(savedItem.getId());
        assertEquals(itemDto.getName(), savedItem.getName());

        savedItem.setName("другое имя");

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.update(savedOwner.getId(), savedItem, 99L)
        );

        assertEquals("There is no item with id 99.", exception.getMessage());

        ItemDto updatedItem = itemService.update(savedOwner.getId(), savedItem, savedItem.getId());

        assertEquals(savedItem.getId(), updatedItem.getId());
        assertNotEquals(itemDto.getName(), updatedItem.getName());
        assertEquals(savedItem.getName(), updatedItem.getName());
    }

    @Test
    @DirtiesContext
    void getById() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getById(savedOwner.getId(), 99L)
        );

        assertEquals("There is no item with id 99.", exception.getMessage());

        ItemDto bookerDto = itemService.getById(savedBooker.getId(), savedItem.getId());

        assertNotNull(bookerDto);
        assertEquals(savedItem.getId(), bookerDto.getId());
        assertNull(bookerDto.getLastBooking());
        assertNull(bookerDto.getNextBooking());

        ItemDto ownerDto = itemService.getById(savedOwner.getId(), savedItem.getId());

        assertNotNull(ownerDto);
        assertEquals(savedItem.getId(), ownerDto.getId());
        assertEquals(ownerDto.getLastBooking().getId(), pastBooking.getId());
        assertEquals(ownerDto.getNextBooking().getId(), futureBooking.getId());
    }

    @Test
    @DirtiesContext
    void get() {
        List<ItemDto> bookerResult = itemService.get(savedBooker.getId(), PAGEABLE);

        assertTrue(bookerResult.isEmpty());

        List<ItemDto> ownerResult = itemService.get(savedOwner.getId(), PAGEABLE);

        assertEquals(3, ownerResult.size());
        assertEquals(1, ownerResult.get(0).getId());
        assertEquals(2, ownerResult.get(1).getId());
        assertEquals(3, ownerResult.get(2).getId());
    }

    @Test
    @DirtiesContext
    void search() {
        List<ItemDto> result = itemService.search("script", PAGEABLE);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
    }

    @Test
    @DirtiesContext
    void addComment() {
        CommentDto commentDto = new CommentDto(null, "comment text", null, null);

        CommentDto result = itemService.addComment(savedBooker.getId(), commentDto, savedItem.getId());

        assertNotNull(result.getId());
        assertEquals(commentDto.getText(), result.getText());
        assertEquals(savedBooker.getName(), result.getAuthorName());
        assertNotNull(result.getCreated());
    }
}