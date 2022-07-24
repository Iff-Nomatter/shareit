package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoOutput {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;
    private Booker booker;
    private Item item;
    private BookingStatus status;

    @Getter
    @AllArgsConstructor
    static class Booker {
        private long id;
    }

    @Getter
    @AllArgsConstructor
    static class Item {
        private long id;
        private String name;
    }
}
