package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    void addBookingRequest() throws Exception {
        BookingDtoInput input = new BookingDtoInput();
        input.setItemId(222L);
        input.setStart(LocalDateTime.now());
        input.setEnd(LocalDateTime.now());

        BookingDtoOutput output = new BookingDtoOutput();
        output.setId(1L);
        output.setItemId(input.getItemId());

        Long bookerId = 333L;
        Mockito.when(bookingService.addBookingRequest(eq(bookerId), Mockito.any(BookingDtoInput.class)))
                .thenReturn(output);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(input))
                        .header("X-Sharer-User-Id", bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(output.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(output.getItemId()), Long.class));
    }

    @Test
    void approveOrRejectBooking() throws Exception {
        BookingDtoOutput output = new BookingDtoOutput();
        output.setId(1L);
        output.setItemId(222L);

        Long bookerId = 333L;
        Mockito.when(bookingService.approveOrRejectBooking(eq(output.getId()), eq(true), eq(bookerId)))
                .thenReturn(output);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(output.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(output.getItemId()), Long.class));
    }

    @Test
    void getBookingById() throws Exception {
        BookingDtoOutput output = new BookingDtoOutput();
        output.setId(1L);
        output.setItemId(222L);

        Long requesterId = 333L;
        Mockito.when(bookingService.getBookingById(eq(output.getId()), eq(requesterId)))
                .thenReturn(output);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", requesterId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(output.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(output.getItemId()), Long.class));
    }

    @Test
    void getAllBookingOfUser() throws Exception {
        BookingDtoOutput output = new BookingDtoOutput();
        output.setId(1L);
        output.setItemId(222L);

        Long requesterId = 333L;
        Mockito.when(bookingService.getAllBookingsOfUser(eq(requesterId), eq(BookingStatus.ALL), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(output));

        mvc.perform(get("/bookings?state=ALL&from=0&size=5")
                        .header("X-Sharer-User-Id", requesterId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(output.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(output.getItemId()), Long.class));
    }

    @Test
    void getAllItemBookingsOfUser() throws Exception {
        BookingDtoOutput output = new BookingDtoOutput();
        output.setId(1L);
        output.setItemId(222L);

        Long requesterId = 333L;
        Mockito.when(bookingService.getAllItemBookingsOfUser(eq(requesterId), eq(BookingStatus.ALL), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(output));

        mvc.perform(get("/bookings/owner?state=ALL&from=0&size=5")
                        .header("X-Sharer-User-Id", requesterId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(output.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(output.getItemId()), Long.class));
    }
}