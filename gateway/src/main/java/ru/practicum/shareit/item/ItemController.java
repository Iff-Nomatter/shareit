package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @GetMapping
    public Object getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId,
                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all items of user={}, from={}, size={}", userId, from, size);
        return itemClient.getAllItemsByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public Object getItem(@RequestHeader(USER_ID_HEADER) long userHeaderId,
                          @PathVariable Long itemId) {
        log.info("Get item {}, userId={}", itemId, userHeaderId);
        return itemClient.getItem(itemId);
    }

    @PatchMapping("/{itemId}")
    public Object updateItem(@RequestHeader(USER_ID_HEADER) long userHeaderId,
                             @PathVariable Long itemId,
                             @RequestBody @Valid ItemUpdateDto itemUpdateDto) {
        log.info("Updating item {}, userId={}", itemUpdateDto, userHeaderId);
        return itemClient.updateItem(userHeaderId, itemUpdateDto, itemId);
    }

    @PostMapping
    public Object createItem(@RequestHeader(USER_ID_HEADER) long userHeaderId,
                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userHeaderId);
        return itemClient.createItem(userHeaderId, itemDto);
    }

    @GetMapping("/search")
    public Object searchItem(@RequestHeader(USER_ID_HEADER) long userId,
                             @RequestParam String text,
                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Searching text={}, from={}, size={}, userId={}", text, from, size, userId);
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public Object postComment(@RequestHeader(USER_ID_HEADER) long userId,
                              @PathVariable long itemId,
                              @RequestBody @Valid CommentDto commentDto) {
        log.info("Posting comment {} to item={}, userId={}", commentDto, itemId, userId);
        return itemClient.postComment(userId, commentDto, itemId);
    }
}
