package ru.practicum.shareit.request.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private static final long USER_ID = 1;
    private ItemRequestDto createRequest;
    private ItemRequestDto returnRequest;
    private List<ItemRequestDto> dtoList;

    @BeforeEach
    void setUp() {
        createRequest = new ItemRequestDto(null, "description", null, null);
        returnRequest = new ItemRequestDto(1L, "description", LocalDateTime.now(), null);
        dtoList = List.of(returnRequest);
    }

    @Test
    void add() throws Exception {
        when(itemRequestService.add(USER_ID, createRequest)).thenReturn(returnRequest);

        String result = mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(returnRequest), result);
    }

    @Test
    void addInvalid() throws Exception {
        ItemRequestDto invalidRequest = new ItemRequestDto(null, null, null, null);

        when(itemRequestService.add(USER_ID, invalidRequest)).thenReturn(returnRequest);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).add(Mockito.anyInt(), Mockito.any(ItemRequestDto.class));
    }

    @Test
    void getYours() throws Exception {
        when(itemRequestService.getYours(USER_ID)).thenReturn(dtoList);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoList), result);
    }

    @Test
    void getTheirs() throws Exception {
        when(itemRequestService.getTheirs(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(dtoList);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoList), result);
    }

    @Test
    void getTheirsInvalidParams() throws Exception {
        when(itemRequestService.getTheirs(Mockito.anyLong(),Mockito.any(Pageable.class))).thenReturn(dtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .contentType("application/json")
                        .param("from", "-1")
                        .param("size", "0")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getTheirs(Mockito.anyLong(), Mockito.any(Pageable.class));
    }

    @Test
    void getById() throws Exception {
        when(itemRequestService.getById(USER_ID, returnRequest.getId())).thenReturn(returnRequest);

        String result = mockMvc
                .perform(MockMvcRequestBuilders.get("/requests/{requestId}", returnRequest.getId())
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(returnRequest), result);
    }
}