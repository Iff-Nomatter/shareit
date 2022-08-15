package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItem() {
        User user = new User();
        user.setId(1);
        user.setName("User1");
        user.setEmail("User1@email.com");
        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        Mockito.when(mockItemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest));

        Mockito.when(mockItemRepository.save(Mockito.any(Item.class)))
                .thenAnswer((Answer<Item>) invocationOnMock -> {
                    Item savedItem = invocationOnMock.getArgument(0);
                    savedItem.setId(1);
                    return savedItem;
                });

        ItemDto testItem = new ItemDto();
        testItem.setName("name");
        testItem.setDescription("description");
        testItem.setRequestId(1L);
        testItem.setAvailable(true);
        ItemDto resultItemDto = itemService.createItem(1, testItem);

        assertEquals(1, resultItemDto.getId());
        assertEquals(testItem.getName(), resultItemDto.getName());
        assertEquals(testItem.getDescription(), resultItemDto.getDescription());
        assertEquals(testItem.getRequestId(), resultItemDto.getRequestId());
        assertEquals(testItem.getAvailable(), resultItemDto.getAvailable());
    }

    @Test
    void updateItem() {
        User user = new User();
        user.setId(1);
        user.setName("User1");
        user.setEmail("User1@email.com");
        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        Item item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("description");
        item.setOwner(user);
        item.setAvailable(true);
        Mockito.when(mockItemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        ItemDto updateItem = new ItemDto();
        updateItem.setName("newname");
        updateItem.setDescription("newdescription");
        updateItem.setAvailable(false);
        ItemDto resultItemDto = itemService.updateItem(1, updateItem, 1);

        assertEquals(item.getId(), resultItemDto.getId());
        assertEquals(updateItem.getName(), resultItemDto.getName());
        assertEquals(updateItem.getDescription(), resultItemDto.getDescription());
        assertEquals(updateItem.getAvailable(), resultItemDto.getAvailable());
    }

    @Test
    void getItemById() {
        User user = new User();
        user.setId(1);

        Item item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("description");
        item.setOwner(user);
        item.setAvailable(true);
        Mockito.when(mockItemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        ItemDtoForOwner resultItemDto = itemService.getItemById(1, 1);
        assertEquals(item.getId(), resultItemDto.getId());
        assertEquals(item.getName(), resultItemDto.getName());
        assertEquals(item.getDescription(), resultItemDto.getDescription());
        assertEquals(item.getAvailable(), resultItemDto.getAvailable());
    }

    @Test
    void getAllItemsByUserId() {
        User user = new User();
        user.setId(1);

        Item itemOne = new Item();
        itemOne.setId(1);
        itemOne.setName("name1");
        itemOne.setDescription("description1");
        itemOne.setOwner(user);
        itemOne.setAvailable(true);

        Item itemTwo = new Item();
        itemTwo.setId(2);
        itemTwo.setName("name2");
        itemTwo.setDescription("description2");
        itemTwo.setOwner(user);
        itemTwo.setAvailable(true);
        List<Item> itemsFromMock = new ArrayList<>(2);
        itemsFromMock.add(itemOne);
        itemsFromMock.add(itemTwo);
        Mockito.when(
                mockItemRepository.findItemsByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class))
        ).thenReturn(itemsFromMock);

        Booking booking = new Booking();
        booking.setId(1);
        User booker = new User();
        booker.setId(2);
        booking.setBooker(booker);
        Mockito.when(mockBookingRepository.findFirstBookingByItemIdAndEndBeforeOrderByStartAsc(Mockito.eq(1L), Mockito.any()))
                .thenReturn(booking);

        List<ItemDtoForOwner> result = itemService.getAllItemsByUserId(1, 0, 5);
        assertEquals(2, result.size());
        ItemDtoForOwner itemDtoOne = result.stream().filter(it -> it.getId() == 1).findAny().orElse(null);
        ItemDtoForOwner itemDtoTwo = result.stream().filter(it -> it.getId() == 2).findAny().orElse(null);

        assertNotNull(itemDtoOne);
        assertNotNull(itemDtoTwo);
        assertEquals(itemDtoOne.getName(), itemOne.getName());
        assertNotNull(itemDtoOne.getLastBooking());
        assertEquals(itemDtoOne.getLastBooking().getId(), booking.getId());
        assertEquals(itemDtoTwo.getName(), itemTwo.getName());
        assertNull(itemDtoTwo.getLastBooking());
    }

    @Test
    void searchItem() {
        Item itemOne = new Item();
        itemOne.setId(1);
        itemOne.setName("name1");
        itemOne.setDescription("description1");
        itemOne.setAvailable(true);

        Item itemTwo = new Item();
        itemTwo.setId(2);
        itemTwo.setName("name2");
        itemTwo.setDescription("description2");
        itemTwo.setAvailable(true);
        List<Item> itemsFromMock = new ArrayList<>(2);
        itemsFromMock.add(itemOne);
        itemsFromMock.add(itemTwo);
        Mockito.when(
                mockItemRepository.searchByNameAndDescriptionAndAvailable(Mockito.anyString(), Mockito.any(Pageable.class))
        ).thenReturn(itemsFromMock);
        List<ItemDto> items = itemService.searchItem("request", 0, 5);
        assertEquals(2, items.size());
    }

    @Test
    void searchItemEmptyRequest() {
        List<ItemDto> items = itemService.searchItem("", 0, 5);
        assertTrue(items.isEmpty());
    }

    @Test
    void postComment() {
        User user = new User();
        user.setId(1);
        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        Item item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("description");
        item.setOwner(user);
        item.setAvailable(true);
        Mockito.when(mockItemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        Booking booking = new Booking();
        booking.setId(1);
        User booker = new User();
        booker.setId(2);
        booking.setBooker(booker);
        Mockito.when(
                        mockBookingRepository.findBookingsByItemIdAndBookerIdAndStatusAndStartBefore(
                                Mockito.eq(1L), Mockito.eq(1L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.singletonList(booking));

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment text");

        Mockito.when(mockCommentRepository.save(Mockito.any(Comment.class))).thenAnswer(
                (Answer<Comment>) invocationOnMock -> {
                    Comment savedComment = invocationOnMock.getArgument(0);
                    savedComment.setId(1);
                    return savedComment;
                });

        CommentDto result = itemService.postComment(user.getId(), item.getId(), commentDto);
        assertEquals(1L, result.getId());
    }

    @Test
    void postCommentNotBooked() {
        User user = new User();
        user.setId(1);
        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        Item item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("description");
        item.setOwner(user);
        item.setAvailable(true);
        Mockito.when(mockItemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        Mockito.when(
                        mockBookingRepository.findBookingsByItemIdAndBookerIdAndStatusAndStartBefore(
                                Mockito.eq(1L), Mockito.eq(1L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.emptyList());

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment text");
        assertThrows(ResponseStatusException.class, () -> itemService.postComment(1L, 1L, commentDto));
    }

    @Test
    void postCommentEmptyText() {
        User user = new User();
        user.setId(1);
        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        Item item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("description");
        item.setOwner(user);
        item.setAvailable(true);
        Mockito.when(mockItemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        Booking booking = new Booking();
        booking.setId(1);
        User booker = new User();
        booker.setId(2);
        booking.setBooker(booker);
        Mockito.when(
                        mockBookingRepository.findBookingsByItemIdAndBookerIdAndStatusAndStartBefore(
                                Mockito.eq(1L), Mockito.eq(1L), Mockito.any(), Mockito.any()))
                .thenReturn(Collections.singletonList(booking));

        CommentDto commentDto = new CommentDto();
        commentDto.setText("");
        assertThrows(ResponseStatusException.class, () -> itemService.postComment(1L, 1L, commentDto));
    }
}