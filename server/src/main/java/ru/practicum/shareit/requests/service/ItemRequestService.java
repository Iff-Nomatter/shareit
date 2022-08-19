package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(long requesterId, ItemRequestDto itemRequestDto);


    ItemRequestDto getItemRequestById(long userId, long itemRequestId);


    List<ItemRequestDto> getAllItemRequestsByUserId(long userId);


    List<ItemRequestDto> getAllItemRequests(long userId, Integer from, Integer size);


}
