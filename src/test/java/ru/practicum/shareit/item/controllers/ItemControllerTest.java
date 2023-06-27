package ru.practicum.shareit.item.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private static final long USER_ID = 1L;
    private ItemDto itemDto = new ItemDto();
    private ItemDto dtoReturn = new ItemDto();
    private List<ItemDto> dtoList;

    @BeforeEach
    void setUp() {
        itemDto.setName("item");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

        dtoReturn.setName("item");
        dtoReturn.setDescription("description");
        dtoReturn.setAvailable(true);
        dtoReturn.setId(1L);

        dtoList = List.of(dtoReturn);
    }

    @Test
    void create() throws Exception {
        when(itemService.add(USER_ID, itemDto)).thenReturn(dtoReturn);

        String result = mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", USER_ID)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoReturn), result);
    }

    @Test
    void createInvalid() throws Exception {
        itemDto.setAvailable(null);

        when(itemService.add(USER_ID, itemDto)).thenReturn(dtoReturn);

        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", USER_ID)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).add(Mockito.anyInt(), Mockito.any(ItemDto.class));
    }

    @Test
    void update() throws Exception {
        dtoReturn.setName("updated-name");

        when(itemService.update(USER_ID, dtoReturn, dtoReturn.getId())).thenReturn(dtoReturn);

        String result = mockMvc.perform(patch("/items/{itemId}", dtoReturn.getId())
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", USER_ID)
                        .content(objectMapper.writeValueAsString(dtoReturn)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoReturn), result);
    }

    @Test
    void getById() throws Exception {
        when(itemService.getById(USER_ID, dtoReturn.getId())).thenReturn(dtoReturn);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", dtoReturn.getId())
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", USER_ID)
                        .content(objectMapper.writeValueAsString(dtoReturn)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoReturn), result);
    }

    @Test
    void get() throws Exception {
        when(itemService.get(USER_ID, 0, 10)).thenReturn(dtoList);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items", 0, 10)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoList), result);
    }

    @Test
    void getInvalidParams() throws Exception {
        when(itemService.get(USER_ID, 0, 10)).thenReturn(dtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/items", 0, 10)
                        .param("from", "-1")
                        .param("size", "0")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).get(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void search() throws Exception {
        when(itemService.search(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(dtoList);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/search", 0, 10)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoList), result);
    }

    @Test
    void addComment() throws Exception {
        CommentDto createComment = new CommentDto(null, "comment", null, null);
        CommentDto returnComment = new CommentDto(1L, "comment", "Vlad", LocalDateTime.now());

        when(itemService.addComment(USER_ID, createComment, dtoReturn.getId())).thenReturn(returnComment);

        String result = mockMvc.perform(post("/items/{itemId}/comment", dtoReturn.getId())
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", USER_ID)
                        .content(objectMapper.writeValueAsString(createComment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(returnComment), result);
    }

    @Test
    void addInvalidComment() throws Exception {
        CommentDto invalidComment = new CommentDto(null, null, null, null);

        when(itemService.addComment(USER_ID, invalidComment, dtoReturn.getId())).thenReturn(invalidComment);

        mockMvc.perform(post("/items/{itemId}/comment", dtoReturn.getId())
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", USER_ID)
                        .content(objectMapper.writeValueAsString(invalidComment)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addComment(Mockito.anyInt(), Mockito.any(CommentDto.class), Mockito.anyInt());
    }
}