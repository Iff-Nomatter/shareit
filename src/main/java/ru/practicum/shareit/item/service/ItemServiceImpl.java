package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public ItemDto createItem(long ownerId, ItemDto itemDto) {
        User user = userRepository.findById(ownerId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует пользователь с id: " + ownerId));
        Item item = ItemMapper.dtoToItem(itemDto);
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
    public ItemDtoForOwner getItemById(long id, long requestorId) {
        Item item = itemRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует предмет с id: " + id));
        if (requestorId == item.getOwner().getId()) {
            return ItemMapper.toOwnerItemDto(item,
                    bookingRepository.findFirstBookingByItemIdAndEndBeforeOrderByStartAsc(item.getId(), LocalDateTime.now()),
                    bookingRepository.findFirstBookingByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now()));
        } else {
            return ItemMapper.toOwnerItemDto(item, null, null);
        }
    }

    @Override
    public List<ItemDtoForOwner> getAllItemsByUserId(long id) {
        List<Item> allItemsByOwnerId = itemRepository.findItemsByOwnerId(id);
        List<ItemDtoForOwner> allItemsDtoByOwnerId = new ArrayList<>();
        for (Item item : allItemsByOwnerId) {
            allItemsDtoByOwnerId.add(ItemMapper.toOwnerItemDto(item,
                    bookingRepository.findFirstBookingByItemIdAndEndBeforeOrderByStartAsc(item.getId(), LocalDateTime.now()),
                    bookingRepository.findFirstBookingByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now())));
        }
        return allItemsDtoByOwnerId;
    }

    @Override
    public List<ItemDto> searchItem(String request) {
        List<ItemDto> resultDto = new ArrayList<>();
        if (request.isBlank()) {
            return resultDto;
        }
        List<Item> result = itemRepository.searchByNameAndDescriptionAndAvailable(request);
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
        if (commentDto.getText().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Комментарий не может быть пустым.");
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
