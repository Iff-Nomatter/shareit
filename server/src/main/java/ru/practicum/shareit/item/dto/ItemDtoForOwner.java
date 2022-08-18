package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoForOwner {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private @Nullable Booking lastBooking;
    private @Nullable Booking nextBooking;
    private Long requestId;


    @Getter
    @AllArgsConstructor
    public static class Booking {
        private long id;
        private long bookerId;
        private LocalDateTime startDate;
    }
}
