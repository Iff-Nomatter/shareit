package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                new ItemRequestDto.Requestor(request.getRequestor().getId()),
                request.getCreated(),
                ItemMapper.toItemDtoList(request.getItems())
        );
    }

    public static ItemRequest dtoToItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        return itemRequest;
    }

    public static List<ItemRequestDto> itemRequestDtoList(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        if (itemRequests == null || itemRequests.isEmpty()) {
            return itemRequestDtos;
        }
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtos.add(ItemRequestMapper.toItemRequestDto(itemRequest));
        }
        return itemRequestDtos;
    }
}
