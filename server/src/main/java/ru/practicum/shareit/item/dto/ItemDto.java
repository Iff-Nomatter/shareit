package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private Long requestId;
}