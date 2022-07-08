package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class BookingDto {
    private LocalDate start;
    private LocalDate end;
    private String item;
    private String booker;
}
