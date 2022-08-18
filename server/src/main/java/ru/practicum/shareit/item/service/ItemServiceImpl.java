package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;


    @Override
    public ItemDto createItem(long ownerId, ItemDto itemDto) {
        User user = userRepository.findById(ownerId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует пользователь с id: " + ownerId));
        Item item = ItemMapper.dtoToItem(itemDto);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Отсутствует запрос с id: " + itemDto.getRequestId()));
            item.setRequest(itemRequest);
        }
        item.setOwner(user);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long ownerId, ItemDto itemDto, long itemId) {
        User user = userRepository.findById(ownerId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует пользователь с id: " + ownerId));
        Item newItem = ItemMapper.dtoToItem(itemDto);
        newItem.setOwner(user);
        Item itemToUpdate = itemRepository.findById(itemId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует предмет с id: " + itemId));
        if (newItem.getOwner() != itemToUpdate.getOwner()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Это не ваш предмет.");
        }
        if (newItem.getName() != null) {
            itemToUpdate.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            itemToUpdate.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            itemToUpdate.setAvailable(newItem.getAvailable());
        }
        itemRepository.save(itemToUpdate);
        return ItemMapper.toItemDto(itemToUpdate);
    }

    @Override
    public ItemDtoForOwner getItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует предмет с id: " + itemId));
        if (userId == item.getOwner().getId()) {
            return ItemMapper.toOwnerItemDto(item,
                    bookingRepository.findFirstBookingByItemIdAndEndBeforeOrderByStartAsc(item.getId(), LocalDateTime.now()),
                    bookingRepository.findFirstBookingByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now()));
        } else {
            return ItemMapper.toOwnerItemDto(item, null, null);
        }
    }

    @Override
    public List<ItemDtoForOwner> getAllItemsByUserId(long id, Integer from, Integer size) {
        List<Item> allItemsByOwnerId =
                new ArrayList<>(itemRepository.findItemsByOwnerId(id, PageRequest.of(from/size, size)));
        List<ItemDtoForOwner> allItemsDtoByOwnerId = new ArrayList<>();
        for (Item item : allItemsByOwnerId) {
            allItemsDtoByOwnerId.add(ItemMapper.toOwnerItemDto(item,
                    bookingRepository.findFirstBookingByItemIdAndEndBeforeOrderByStartAsc(item.getId(), LocalDateTime.now()),
                    bookingRepository.findFirstBookingByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now())));
        }
        allItemsDtoByOwnerId.sort((o1, o2) -> {
            if (o1.getNextBooking() == null)
                return o2.getNextBooking() == null ? 0 : 1;
            if (o2.getNextBooking() == null)
                return -1;
            return o2.getNextBooking().getStartDate().compareTo(o1.getNextBooking().getStartDate());
        });
        return allItemsDtoByOwnerId;
    }

    @Override
    public List<ItemDto> searchItem(String request, Integer from, Integer size) {
        List<ItemDto> resultDto = new ArrayList<>();
        if (request.isBlank()) {
            return resultDto;
        }
        List<Item> result = itemRepository.searchByNameAndDescriptionAndAvailable(request, PageRequest.of(from/size, size));
        for (Item item : result) {
            resultDto.add(ItemMapper.toItemDto(item));
        }
        return resultDto;
    }

    @Override
    public CommentDto postComment(long userId, long itemId, CommentDto commentDto) {
        User user = getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);
        List<Booking> booking = bookingRepository
                .findBookingsByItemIdAndBookerIdAndStatusAndStartBefore(itemId,
                        userId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now());
        if (booking == null || booking.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Вы эту вещь не бронировали.");
        }
        Comment comment = CommentMapper.dtoToComment(commentDto, user, item);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    private User getUserOrThrow(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует пользователь с id: " + id));
    }

    private Item getItemOrThrow(long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует предмет с id: " + id));
    }
}
