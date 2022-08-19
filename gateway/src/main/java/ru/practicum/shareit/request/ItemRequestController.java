package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public Object createItemRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Creating request {}, userId={}", itemRequestDto, userId);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public Object getAllItemRequestsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get item requests of userId={}", userId);
        return itemRequestClient.getAllItemRequestsOfUser(userId);
    }

    @GetMapping("/{requestId}")
    public Object getItemRequestById(@RequestHeader(USER_ID_HEADER) long userId,
                                     @PathVariable long requestId) {
        log.info("Get itemRequestId={}, userId={}", requestId, userId);
        return itemRequestClient.getItemRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public Object getAllItemRequestsPaginated(@RequestHeader(USER_ID_HEADER) long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get item requests, userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }
}
