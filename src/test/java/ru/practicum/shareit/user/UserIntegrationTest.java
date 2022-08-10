package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void getAllUsers() {
        User userOne = new User();
        userOne.setName("name1");
        userOne.setEmail("name1@email.com");
        userRepository.saveAndFlush(userOne);

        List<UserDto> userDtos = userService.getAllUsers();
        assertEquals(1, userDtos.size());
        assertEquals(userOne.getName(), userDtos.get(0).getName());
        assertEquals(userOne.getEmail(), userDtos.get(0).getEmail());
    }

    @Test
    void createUser() {
        UserDto user = new UserDto();
        user.setName("name2");
        user.setEmail("name2@email.com");
        userService.createUser(user);

        List<User> users = userRepository.findAll();
        assertFalse(users.isEmpty());
        Optional<User> userFromDb = users.stream().filter(u -> u.getName().equals(user.getName())).findAny();
        assertTrue(userFromDb.isPresent());
        assertEquals(user.getEmail(), userFromDb.get().getEmail());
    }

}
