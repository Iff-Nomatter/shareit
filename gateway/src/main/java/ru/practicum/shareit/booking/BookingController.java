package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private static final String USER_ID_HEADER = "X-Sharer-User-Id";
	private final BookingClient bookingClient;

	@GetMapping
	public Object getBookings(@RequestHeader(USER_ID_HEADER) long userId,
							  @RequestParam(name = "state", defaultValue = "all") String stateParam,
							  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
							  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState
				.from(stateParam)
				.orElseThrow(() -> new IllegalStateException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public Object bookItem(@RequestHeader(USER_ID_HEADER) long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public Object getBooking(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public Object updateBooking(@RequestHeader(USER_ID_HEADER) long userId,
								@PathVariable Long bookingId,
								@RequestParam("approved") Boolean isApproved) {
		log.info("Updating bookingId={} to {} , iserId={}", bookingId, isApproved, userId);
		return bookingClient.updateBooking(userId, bookingId, isApproved);
	}

	@GetMapping("/owner")
	public Object getBookingsOfOwner(@RequestHeader(USER_ID_HEADER) long userId,
							  @RequestParam(name = "state", defaultValue = "all") String stateParam,
							  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
							  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState
				.from(stateParam)
				.orElseThrow(() -> new IllegalStateException("Unknown state: " + stateParam));
		log.info("Get booking of userId={}, with state {}, from={}, size={}", userId, stateParam, from, size);
		return bookingClient.getBookingsOfOwner(userId, state, from, size);
	}
}
