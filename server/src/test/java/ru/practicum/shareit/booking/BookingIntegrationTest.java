package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SpringBootTest
@Transactional
public class BookingIntegrationTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void getAllBookingsOfUser() {
        User user = new User();
        user.setName("owner");
        user.setEmail("owner@email.com");
        userRepository.saveAndFlush(user);

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@email.com");
        userRepository.saveAndFlush(booker);

        Item item = new Item();
        item.setOwner(user);
        item.setName("ru/practicum/shareit/item");
        item.setDescription("description");
        item.setAvailable(true);
        itemRepository.saveAndFlush(item);

        Booking lastBooking = new Booking();
        lastBooking.setItem(item);
        lastBooking.setStatus(BookingStatus.APPROVED);
        lastBooking.setBooker(booker);
        lastBooking.setStart(LocalDateTime.now().minus(2, ChronoUnit.DAYS));
        lastBooking.setEnd(LocalDateTime.now().minus(1, ChronoUnit.DAYS));
        bookingRepository.saveAndFlush(lastBooking);

        Booking nextBooking = new Booking();
        nextBooking.setItem(item);
        nextBooking.setStatus(BookingStatus.APPROVED);
        nextBooking.setBooker(booker);
        nextBooking.setStart(LocalDateTime.now().plus(2, ChronoUnit.DAYS));
        nextBooking.setEnd(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
        bookingRepository.saveAndFlush(nextBooking);

        List<BookingDtoOutput> past = bookingService.getAllBookingsOfUser(booker.getId(), BookingStatus.PAST, 0, 5);
        Assertions.assertFalse(past.isEmpty());
        Assertions.assertEquals(lastBooking.getId(), past.get(0).getId());

        List<BookingDtoOutput> future = bookingService.getAllBookingsOfUser(booker.getId(), BookingStatus.FUTURE, 0, 5);
        Assertions.assertFalse(future.isEmpty());
        Assertions.assertEquals(nextBooking.getId(), future.get(0).getId());
    }

    @Test
    void getAllItemBookingsOfUser() {
        User user = new User();
        user.setName("owner");
        user.setEmail("owner@email.com");
        userRepository.saveAndFlush(user);

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@email.com");
        userRepository.saveAndFlush(booker);

        Item item = new Item();
        item.setOwner(user);
        item.setName("ru/practicum/shareit/item");
        item.setDescription("description");
        item.setAvailable(true);
        itemRepository.saveAndFlush(item);

        Booking lastBooking = new Booking();
        lastBooking.setItem(item);
        lastBooking.setStatus(BookingStatus.APPROVED);
        lastBooking.setBooker(booker);
        lastBooking.setStart(LocalDateTime.now().minus(2, ChronoUnit.DAYS));
        lastBooking.setEnd(LocalDateTime.now().minus(1, ChronoUnit.DAYS));
        bookingRepository.saveAndFlush(lastBooking);

        Booking nextBooking = new Booking();
        nextBooking.setItem(item);
        nextBooking.setStatus(BookingStatus.APPROVED);
        nextBooking.setBooker(booker);
        nextBooking.setStart(LocalDateTime.now().plus(2, ChronoUnit.DAYS));
        nextBooking.setEnd(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
        bookingRepository.saveAndFlush(nextBooking);

        List<BookingDtoOutput> past = bookingService.getAllItemBookingsOfUser(user.getId(), BookingStatus.PAST, 0, 5);
        Assertions.assertFalse(past.isEmpty());
        Assertions.assertEquals(lastBooking.getId(), past.get(0).getId());

        List<BookingDtoOutput> future = bookingService.getAllItemBookingsOfUser(user.getId(), BookingStatus.FUTURE, 0, 5);
        Assertions.assertFalse(future.isEmpty());
        Assertions.assertEquals(nextBooking.getId(), future.get(0).getId());
    }
}
