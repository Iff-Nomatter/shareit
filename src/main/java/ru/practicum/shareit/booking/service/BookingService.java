package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;

import java.util.List;

public interface BookingService {
    BookingDtoOutput addBookingRequest(long bookerId, BookingDtoInput bookingDtoInput);

    BookingDtoOutput approveOrRejectBooking(long bookingId, boolean approved, long requesterId);

    BookingDtoOutput getBookingById(long bookingId, long requesterId);

    List<BookingDtoOutput> getAllBookingsOfUser(long requesterId, BookingStatus state);

    List<BookingDtoOutput> getAllItemBookingsOfUser(long ownerId, BookingStatus state);
}
