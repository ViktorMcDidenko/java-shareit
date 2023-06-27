package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplIntegrationTest {
    @Autowired
    UserService userService;

    private UserDto user1;
    private UserDto savedUser1;
    private UserDto savedUser2;

    @BeforeEach
    void setUp() {
        user1 = new UserDto(null, "name1", "ya1@mail.ru");
        savedUser1 = userService.add(user1);
        UserDto user2 = new UserDto(null, "name2", "ya2@mail.ru");
        savedUser2 = userService.add(user2);
    }

    @Test
    @DirtiesContext
    void add() {
        assertNotNull(savedUser1.getId());
        assertEquals(savedUser1.getEmail(), user1.getEmail());
    }

    @Test
    @DirtiesContext
    void update() {
        savedUser1.setName("updatedName");

        UserDto updatedUser = userService.update(savedUser1.getId(), savedUser1);

        assertEquals(savedUser1.getId(), updatedUser.getId());
        assertNotEquals(user1.getName(), updatedUser.getName());
        assertEquals(savedUser1.getName(), updatedUser.getName());
    }

    @Test
    @DirtiesContext
    void getAllAndDelete() {
        List<UserDto> result = userService.getAll();

        assertEquals(2, result.size());
        assertEquals(savedUser1.getId(), result.get(0).getId());
        assertEquals(savedUser2.getId(), result.get(1).getId());

        userService.delete(savedUser2.getId());

        List<UserDto> resultAfterDelete = userService.getAll();

        assertEquals(1, resultAfterDelete.size());
        assertEquals(savedUser1.getId(), result.get(0).getId());
    }

    @Test
    @DirtiesContext
    void getById() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getById(99L)
        );

        assertEquals("There is no user with id 99.", exception.getMessage());

        UserDto result = userService.getById(savedUser1.getId());

        assertEquals(savedUser1.getId(), result.getId());
    }
}