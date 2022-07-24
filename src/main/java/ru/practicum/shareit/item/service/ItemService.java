package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long ownerId, ItemDto itemDto);

    ItemDto updateItem(long ownerId, ItemDto itemDto, long itemId);

    ItemDtoForOwner getItemById(long id, long requesterId);

    List<ItemDtoForOwner> getAllItemsByUserId(long id);

    List<ItemDto> searchItem(String request);

    CommentDto postComment(long userId, long itemId, CommentDto commentDto);
}
