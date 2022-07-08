package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(long ownerId, ItemDto itemDto) {
        checkUserPresence(ownerId);
        Item newItem = ItemMapper.dtoToItem(itemDto);
        newItem.setOwner(userRepository.getUserById(ownerId));
        return ItemMapper.toItemDto(itemRepository.createItem(newItem));
    }

    @Override
    public ItemDto updateItem(long ownerId, ItemDto itemDto, long itemId) {
        checkUserPresence(ownerId);
        Item newItem = ItemMapper.dtoToItem(itemDto);
        newItem.setOwner(userRepository.getUserById(ownerId));
        Item itemForUpdate = itemRepository.getItemById(itemId);
        if (newItem.getOwner() != itemForUpdate.getOwner()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Это не ваш предмет.");
        }
        if (newItem.getName() != null) {
            itemForUpdate.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            itemForUpdate.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            itemForUpdate.setAvailable(newItem.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.updateItem(itemForUpdate));
    }

    @Override
    public ItemDto getItemById(long id) {
        return ItemMapper.toItemDto(itemRepository.getItemById(id));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long id) {
        List<Item> itemsWithOwner = itemRepository.getAllItemsByUserId(id);
        List<ItemDto> itemsDtoWithOwner = new ArrayList<>();
        for (Item item : itemsWithOwner) {
            itemsDtoWithOwner.add(ItemMapper.toItemDto(item));
        }
        return itemsDtoWithOwner;
    }

    @Override
    public List<ItemDto> searchItem(String request) {
        List<Item> foundItems = itemRepository.searchItem(request);
        List<ItemDto> foundItemsDto = new ArrayList<>();
        for (Item item : foundItems) {
            if (item.getAvailable()) {
                foundItemsDto.add(ItemMapper.toItemDto(item));
            }
        }
        return foundItemsDto;
    }

    private void checkUserPresence(long id) {
        if (userRepository.getUserById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Отсутствует пользователь с id: " + id);
        }
    }
}
