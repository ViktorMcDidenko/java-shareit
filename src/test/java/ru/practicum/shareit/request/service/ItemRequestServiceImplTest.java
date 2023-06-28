package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRequestServiceImplTest {
    @Autowired
    ItemRequestService itemRequestService;

    @Autowired
    UserRepository userRepository;

    private User savedRequestor;
    private User otherRequestor;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto savedItemRequestDto;

    @BeforeEach
    void setUp() {
        User requestor = new User("requestor", "ya@ya.ru"); //id1
        savedRequestor = userRepository.save(requestor);
        itemRequestDto = new ItemRequestDto(null, "description", null, null);
        savedItemRequestDto = itemRequestService.add(savedRequestor.getId(), itemRequestDto);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(null, "description2", null, null);
        itemRequestService.add(savedRequestor.getId(), itemRequestDto2);
        otherRequestor = new User("requestor2", "ya2@ya.ru"); //id2
        User savedOtherRequestor = userRepository.save(otherRequestor);
        ItemRequestDto itemRequestDto3 = new ItemRequestDto(null, "description3", null, null);
        itemRequestService.add(savedOtherRequestor.getId(), itemRequestDto3);
    }

    @Test
    @DirtiesContext
    void add() {
        assertNotNull(savedItemRequestDto.getId());
        assertNotNull(savedItemRequestDto.getCreated());
        assertEquals(itemRequestDto.getDescription(), savedItemRequestDto.getDescription());
    }

    @Test
    @Transactional
    @DirtiesContext
    void getYours() {
        List<ItemRequestDto> requestorResult = itemRequestService.getYours(savedRequestor.getId());

        assertEquals(2, requestorResult.size());
        assertEquals(2, requestorResult.get(0).getId());
        assertEquals(1, requestorResult.get(1).getId());
    }

    @Test
    @Transactional
    @DirtiesContext
    void getTheirs() {
        List<ItemRequestDto> requestorResultByOtherRequestor =
                itemRequestService.getTheirs(otherRequestor.getId(), PageRequest.of(0, 10));

        assertEquals(2, requestorResultByOtherRequestor.size());
        assertEquals(2, requestorResultByOtherRequestor.get(0).getId());
        assertEquals(1, requestorResultByOtherRequestor.get(1).getId());
    }

    @Test
    @Transactional
    @DirtiesContext
    void getById() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getById(savedRequestor.getId(), 99L)
        );

        assertEquals("There is no request with id 99.", exception.getMessage());

        ItemRequestDto result = itemRequestService.getById(savedRequestor.getId(), savedItemRequestDto.getId());

        assertEquals(savedItemRequestDto.getId(), result.getId());
    }
}