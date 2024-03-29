package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

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
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) long userId,
                              @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID_HEADER) long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) {
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoForOwner getItemById(@RequestHeader(USER_ID_HEADER) long userId,
                                       @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoForOwner> getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId,
                                                     @RequestParam(required = false, value = "from") Integer from,
                                                     @RequestParam(required = false, value = "size") Integer size) {
        return itemService.getAllItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestParam(required = false, value = "from") Integer from,
                                    @RequestParam(required = false, value = "size") Integer size) {
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto postComment(@RequestHeader(USER_ID_HEADER) long userId,
                                  @PathVariable long itemId,
                                  @RequestBody CommentDto commentDto) {
        return itemService.postComment(userId, itemId, commentDto);
    }
}
