package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoOutput addBookingRequest(@RequestHeader(USER_ID_HEADER) long bookerId,
                                              @RequestBody BookingDtoInput bookingDtoInput) {
        return bookingService.addBookingRequest(bookerId, bookingDtoInput);
    }

    @PostMapping("/{bookingId}")
    public BookingDtoOutput approveOrRejectBooking(@PathVariable long bookingId,
                                                   @RequestParam("approved") Boolean isApproved,
                                                   @RequestHeader(USER_ID_HEADER) long requesterId) {
        return bookingService.approveOrRejectBooking(bookingId, isApproved, requesterId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutput getBookingById(@RequestHeader(USER_ID_HEADER) long requesterId,
                                           @PathVariable long bookingId) {
        return bookingService.getBookingById(bookingId, requesterId);
    }

    @GetMapping
    public List<BookingDtoOutput> getAllBookingOfUser(@RequestParam(name = "state", defaultValue = "ALL")
                                                      BookingStatus state,
                                                      @RequestHeader(USER_ID_HEADER) long requesterId,
                                                      @RequestParam(required = false, value = "from") Integer from,
                                                      @RequestParam(required = false, value = "size") Integer size) {
        return bookingService.getAllBookingsOfUser(requesterId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> getAllItemBookingsOfUser(@RequestParam(name = "state", defaultValue = "ALL")
                                                           BookingStatus state,
                                                           @RequestHeader(USER_ID_HEADER) long ownerId,
                                                           @RequestParam(required = false, value = "from") Integer from,
                                                           @RequestParam(required = false, value = "size") Integer size) {
        return bookingService.getAllItemBookingsOfUser(ownerId, state, from, size);
    }
}
