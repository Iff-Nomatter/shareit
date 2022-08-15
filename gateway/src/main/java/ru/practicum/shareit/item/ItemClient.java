package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public Object getItem(long itemId) {
        return get("/" + itemId, itemId);
    }

    public Object getAllItemsByUserId(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get(UriComponentsBuilder.fromPath("")
                .queryParam("from", "{from}")
                .queryParam("size", "{size}")
                .encode()
                .toUriString(), userId, parameters);
    }

    public Object updateItem(long userId, ItemUpdateDto itemUpdateDto, long itemId) {
        return post("/" + itemId, userId, itemUpdateDto);
    }

    public Object createItem(long userId, ItemDto itemDto) {
        return post("",userId, itemDto);
    }

    public Object searchItem(long userId, String searchRequest, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", searchRequest,
                "from", from,
                "size", size
        );
        return get(UriComponentsBuilder.fromPath("/search")
                .queryParam("text", "{text}")
                .queryParam("from", "{from}")
                .queryParam("size", "{size}")
                .encode()
                .toUriString(), userId, parameters);
    }

    public Object postComment(long userId, CommentDto commentDto, long itemId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
