package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @JsonProperty(value = "available", required = true)
    @NotNull
    private Boolean available;
    private List<CommentDto> comments;
    private Long requestId;
}