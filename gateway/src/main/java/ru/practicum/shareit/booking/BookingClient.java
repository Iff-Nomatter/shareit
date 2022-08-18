package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public Object getBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get(UriComponentsBuilder.fromPath("")
                .queryParam("state", "{state}")
                .queryParam("from", "{from}")
                .queryParam("size", "{size}")
                .encode()
                .toUriString(), userId, parameters);
    }

    public Object getBookingsOfOwner(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get(UriComponentsBuilder.fromPath("/owner")
                .queryParam("state", "{state}")
                .queryParam("from", "{from}")
                .queryParam("size", "{size}")
                .encode()
                .toUriString(), userId, parameters);
    }

    public Object bookItem(long userId, BookItemRequestDto requestDto) {
        if (requestDto.getStart().isAfter(requestDto.getEnd()) ||
                requestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Время аренды не укладывается в реалистичные рамки.");
        }
        return post("", userId, requestDto);
    }

    public Object updateBooking(long userId, long bookingId, Boolean isApproved) {
        Map<String, Object> parameters = Map.of(
                "approved", String.valueOf(isApproved)
        );
        return post(UriComponentsBuilder.fromPath("/" + bookingId)
                .queryParam("approved", "{approved}")
                .encode()
                .toUriString(), userId, parameters, null);
    }

    public Object getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }
}
