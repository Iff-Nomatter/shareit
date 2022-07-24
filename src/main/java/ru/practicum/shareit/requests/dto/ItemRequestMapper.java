package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.requests.model.ItemRequest;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getDescription(),
                request.getRequestor(),
                request.getCreated()
        );
    }
}
