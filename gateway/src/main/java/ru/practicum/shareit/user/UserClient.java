package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Map;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public Object getUser(long userId) {
        return get("/" + userId, userId);
    }

    public Object getAllUsers(Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get(UriComponentsBuilder.fromPath("")
                        .queryParam("from", "{from}")
                        .queryParam("size", "{size}")
                        .encode()
                        .toUriString(),
                0, parameters);
    }

    public Object updateUser(long userId, UserUpdateDto userUpdateDto) {
        return post("/" + userId, 0, userUpdateDto);
    }

    public Object createUser(UserDto userDto) {
        return post("", 0, userDto);
    }

    public Object deleteUser(long userId) {
        return delete("/" + userId, userId);
    }
}
