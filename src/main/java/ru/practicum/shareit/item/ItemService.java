package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long ownerId, ItemDto itemDto);

    ItemDto updateItem(long ownerId, ItemDto itemDto, long itemId);

    ItemDto getItemById(long id);

    List<ItemDto> getAllItemsByUserId(long id);

    List<ItemDto> searchItem(String request);
}
