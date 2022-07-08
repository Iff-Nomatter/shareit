package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long idCounter = 0;

    @Override
    public Item createItem(Item item) {
        item.setId(generateItemId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAllItemsByUserId(long id) {
        List<Item> itemsWithOwner = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId() == id) {
                itemsWithOwner.add(item);
            }
        }
        return itemsWithOwner;
    }

    @Override
    public List<Item> searchItem(String request) {
        List<Item> foundItems = new ArrayList<>();
        if (request.isEmpty() || request.isBlank()) {
            return foundItems;
        }
        for (Item item : items.values()) {
            request = request.toLowerCase();
            if (item.getName().toLowerCase().contains(request) ||
                    item.getDescription().toLowerCase().contains(request)) {
                foundItems.add(item);
            }
        }
        return foundItems;
    }

    private long generateItemId() {
        idCounter++;
        return idCounter;
    }
}
