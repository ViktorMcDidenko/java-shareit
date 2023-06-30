package ru.practicum.shareit.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private UserDto dto;
    private UserDto returnDto;

    @BeforeEach
    void setUp() {
        dto = new UserDto(null, "name", "mail@mail.ru");
        returnDto = new UserDto(1L,"name", "mail@mail.ru");
    }

    @Test
    void create() throws Exception {
        when(userService.add(dto)).thenReturn(returnDto);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(returnDto), result);
    }

    @Test
    void createInvalid() throws Exception {
        UserDto invalidDto = new UserDto(null, "name", null);

        when(userService.add(invalidDto)).thenReturn(returnDto);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).add(Mockito.any(UserDto.class));
    }

    @Test
    void update() throws Exception {
        UserDto updatedDto = returnDto;
        updatedDto.setName("updatedName");

        when(userService.update(returnDto.getId(), updatedDto)).thenReturn(updatedDto);

        String result = mockMvc.perform(patch("/users/{userId}", updatedDto.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(updatedDto), result);
    }

    @Test
    void findAll() throws Exception {
        List<UserDto> dtoList = List.of(returnDto);

        when(userService.getAll()).thenReturn(dtoList);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoList), result);
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", Mockito.anyLong()))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(Mockito.anyLong());
    }

    @Test
    void findById() throws Exception {
        when(userService.getById(returnDto.getId())).thenReturn(returnDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", returnDto.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(returnDto), result);
    }
}