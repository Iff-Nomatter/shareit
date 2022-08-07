package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    private UserRepository mockUserRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void init() {
        mockUserRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(mockUserRepository);
    }

    @Test
    void createUser() {
        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setName("User1");
        expectedUser.setEmail("User1@email.com");
        Mockito.when(mockUserRepository.save(Mockito.any(User.class))).thenReturn(expectedUser);

        UserDto testUser = new UserDto();
        testUser.setName("User1");
        testUser.setEmail("User1@email.com");
        UserDto actualUser = userService.createUser(testUser);

        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
    }

    @Test
    void createUserEmailException() {
        Mockito.when(mockUserRepository.save(Mockito.any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(ResponseStatusException.class, () -> userService.createUser(new UserDto()));
    }

    @Test
    void updateUser() {
        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setName("User1");
        expectedUser.setEmail("User1@email.com");
        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expectedUser));
        Mockito.when(mockUserRepository.save(Mockito.any(User.class))).thenReturn(expectedUser);

        UserDto updatedUser = new UserDto();
        updatedUser.setName("UpdatedUser1");
        updatedUser.setEmail("UpdatedUser1@mail.ru");
        UserDto actualUser = userService.updateUser(1, updatedUser);

        assertEquals(1, actualUser.getId());
        assertEquals(updatedUser.getName(), actualUser.getName());
        assertEquals(updatedUser.getEmail(), actualUser.getEmail());
    }

    @Test
    void deleteUser() {
        userService.deleteUser(1);
        Mockito.verify(mockUserRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deleteUserNotFoundException() {
        Mockito.doThrow(EmptyResultDataAccessException.class).when(mockUserRepository).deleteById(1L);

        assertThrows(ResponseStatusException.class, () -> userService.deleteUser(1));
    }

    @Test
    void getUserById() {
        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setName("User1");
        expectedUser.setEmail("User1@email.com");

        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expectedUser));
        UserDto resultUser = userService.getUserById(1);
        assertEquals(1, resultUser.getId());
        assertEquals(expectedUser.getName(), resultUser.getName());
        assertEquals(expectedUser.getEmail(), resultUser.getEmail());
    }

    @Test
    void getAllUsers() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("user1");
        user1.setEmail("user@1.ru");
        User user2 = new User();
        user2.setId(2);
        user2.setName("user2");
        user2.setEmail("user@2.com");
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        Mockito.when(mockUserRepository.findAll()).thenReturn(users);

        List<UserDto> allUsers = userService.getAllUsers();

        assertEquals(users.size(), allUsers.size());
    }
}