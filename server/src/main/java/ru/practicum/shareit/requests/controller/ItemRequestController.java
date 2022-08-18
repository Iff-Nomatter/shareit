package ru.practicum.shareit.requests.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(USER_ID_HEADER) long requesterId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(requesterId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllItemRequestsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.getAllItemRequestsByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }

    @GetMapping(value = "/all")
    public List<ItemRequestDto> getAllItemRequestsPagitaned(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(required = false, value = "from") Integer from,
            @RequestParam(required = false, value = "size") Integer size
    ) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }
}
