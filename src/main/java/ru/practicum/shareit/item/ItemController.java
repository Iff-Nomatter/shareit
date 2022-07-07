package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@NotEmpty @RequestHeader(USER_ID_HEADER) long id,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(id, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@NotEmpty @RequestHeader(USER_ID_HEADER) long id,
                              @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) {
        return itemService.updateItem(id, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@NotEmpty @RequestHeader(USER_ID_HEADER) long id,
                               @PathVariable long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@NotEmpty @RequestHeader(USER_ID_HEADER) long id) {
        return itemService.getAllItemsByUserId(id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }
}
