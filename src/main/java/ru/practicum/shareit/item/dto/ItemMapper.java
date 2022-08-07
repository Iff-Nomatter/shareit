package ru.practicum.shareit.item.dto;

import org.springframework.lang.Nullable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                CommentMapper.toCommentDtoList(item.getComments()),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        List<ItemDto> itemDtos = new ArrayList<>();
        if (items == null || items.isEmpty()) {
            return itemDtos;
        }
        for (Item item : items) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    public static ItemDtoForOwner toOwnerItemDto(Item item,
                                         @Nullable Booking lastBooking,
                                         @Nullable Booking nextBooking) {
        return new ItemDtoForOwner(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                CommentMapper.toCommentDtoList(item.getComments()),
                lastBooking != null
                        ? new ItemDtoForOwner.Booking(lastBooking.getId(), lastBooking.getBooker().getId())
                        : null,
                nextBooking != null
                        ? new ItemDtoForOwner.Booking(nextBooking.getId(), nextBooking.getBooker().getId())
                        : null,
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item dtoToItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}
