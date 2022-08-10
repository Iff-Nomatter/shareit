package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private long id;
    @NotEmpty
    private String description;
    private Requestor requestor;
    private LocalDateTime created;
    private List<ItemDto> items;


    @Getter
    @AllArgsConstructor
    public static class Requestor {
        private long id;
    }
}
