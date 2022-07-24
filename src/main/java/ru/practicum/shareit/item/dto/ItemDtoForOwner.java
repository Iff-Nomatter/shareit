package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoForOwner {
    private long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @JsonProperty(value = "available", required = true)
    @NotNull
    private Boolean available;
    private List<CommentDto> comments;
    private @Nullable Booking lastBooking;
    private @Nullable Booking nextBooking;


    @Getter
    @AllArgsConstructor
    static class Booking {
        private long id;
        private long bookerId;
    }
}
