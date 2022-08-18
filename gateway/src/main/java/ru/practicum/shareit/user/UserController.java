package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public Object getAllUsers(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all users from={}, size={}", from, size);
        return userClient.getAllUsers(from, size);
    }

    @GetMapping("/{userId}")
    public Object getUser(@PathVariable Long userId) {
        log.info("Get user {}", userId);
        return userClient.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public Object updateUser(@PathVariable Long userId,
                             @RequestBody @Valid UserUpdateDto userUpdateDto) {
        log.info("Updating user {}", userUpdateDto);
        return userClient.updateUser(userId, userUpdateDto);
    }

    @PostMapping
    public Object createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.createUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public Object deleteUser(@PathVariable Long userId) {
        log.info("Deleting user {}", userId);
        return userClient.deleteUser(userId);
    }
}
