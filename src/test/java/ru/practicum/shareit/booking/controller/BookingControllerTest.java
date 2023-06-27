package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private User booker;
    private User owner;
    private BookingDtoCreate dtoCreate;
    private BookingDto dtoReturn;
    private BookingDto dtoApproved;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "booker", "a@mail.ru");
        owner = new User(2L, "owner", "a2@mail.ru");
        Item item = new Item(owner, "item", "itemDescription");
        dtoCreate = new BookingDtoCreate(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        dtoReturn = new BookingDto(1L, dtoCreate.getStart(), dtoCreate.getEnd(), Status.WAITING, booker, item);
        dtoApproved = new BookingDto(1L, dtoCreate.getStart(), dtoCreate.getEnd(), Status.APPROVED, booker, item);
    }

    @Test
    void add() throws Exception {
        when(bookingService.add(booker.getId(), dtoCreate))
                .thenReturn(dtoReturn);

        String result = mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(objectMapper.writeValueAsString(dtoCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoReturn), result);
    }

    @Test
    void addInvalid() throws Exception {
        BookingDtoCreate invalidDto = new BookingDtoCreate(null,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1));

        when(bookingService.add(booker.getId(), invalidDto)).thenReturn(dtoReturn);

        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).add(Mockito.anyInt(), Mockito.any(BookingDtoCreate.class));
    }

    @Test
    void approve() throws Exception {
        when(bookingService.approve(owner.getId(), dtoReturn.getId(), true))
                .thenReturn(dtoApproved);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", dtoReturn.getId())
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoApproved), result);
    }

    @Test
    void getById() throws Exception {
        when(bookingService.getById(owner.getId(), dtoReturn.getId())).thenReturn(dtoReturn);

        String result = mockMvc.perform(get("/bookings/{bookingId}", dtoReturn.getId())
                        .header("X-Sharer-User-Id", owner.getId())).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoReturn), result);
    }

    @Test
    void getAllBooker() throws Exception {
        when(bookingService.getAllBooker(booker.getId(), State.ALL.toString(), 0, 10))
                .thenReturn(List.of(dtoReturn));

        String result = mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", booker.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(dtoReturn)), result);
    }

    @Test
    void getAllBookerInvaidParams() throws Exception {
        when(bookingService.getAllBooker(booker.getId(), State.ALL.toString(), 0, 10))
                .thenReturn(List.of(dtoReturn));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "0")
                        .header("X-Sharer-User-Id", booker.getId()))
                .andExpect(status().isBadRequest());

        verify(bookingService,
                never()).getAllBooker(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void getAllOwner() throws Exception {
        when(bookingService.getAllOwner(owner.getId(), State.ALL.toString(), 0, 10))
                .thenReturn(List.of(dtoReturn));

        String result = mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", owner.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(dtoReturn)), result);
    }
}