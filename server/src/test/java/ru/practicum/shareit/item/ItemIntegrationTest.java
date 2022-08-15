package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
public class ItemIntegrationTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    void getAllItemsByUserId() {
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

        List<ItemDtoForOwner> items = itemService.getAllItemsByUserId(user.getId(), 0, 5);
        assertFalse(items.isEmpty());
        assertEquals(item.getName(), items.get(0).getName());
        assertEquals(lastBooking.getId(), items.get(0).getLastBooking().getId());
        assertEquals(nextBooking.getId(), items.get(0).getNextBooking().getId());
    }

    @Test
    void postComment() {
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

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test comment");

        itemService.postComment(booker.getId(), item.getId(), commentDto);

        List<Comment> comments = commentRepository.findAll();
        assertFalse(comments.isEmpty());
        assertEquals(commentDto.getText(), comments.get(0).getText());
        assertEquals(item.getId(), comments.get(0).getItem().getId());
    }
}
