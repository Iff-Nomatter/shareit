package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static BookingDtoOutput toBookingDtoOutput(Booking booking) {
        BookingDtoOutput bookingDtoOutput = new BookingDtoOutput();
        bookingDtoOutput.setId(booking.getId());
        bookingDtoOutput.setStart(booking.getStart());
        bookingDtoOutput.setEnd(booking.getEnd());
        bookingDtoOutput.setItem(new BookingDtoOutput.Item(booking.getItem().getId(), booking.getItem().getName()));
        bookingDtoOutput.setBooker(new BookingDtoOutput.Booker(booking.getBooker().getId()));
        bookingDtoOutput.setStatus(booking.getStatus());
        return bookingDtoOutput;
    }

    public static Booking dtoToBooking(BookingDtoInput bookingDtoInput) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoInput.getStart());
        booking.setEnd(bookingDtoInput.getEnd());
        return booking;
    }

    public static List<BookingDtoOutput> convertBookingToDtoOutput(List<Booking> bookingList) {
        List<BookingDtoOutput> bookingDtoList = new ArrayList<>();
        for (Booking booking : bookingList) {
            bookingDtoList.add(BookingMapper.toBookingDtoOutput(booking));
        }
        return bookingDtoList;
    }
}
