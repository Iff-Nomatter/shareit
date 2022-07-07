package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto createUser(UserDto userDto) {
        checkForEmailDuplicate(userDto);
        User newUser = UserMapper.dtoToUser(userDto);
        return UserMapper.toUserDto(repository.createUser(newUser));
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        if (repository.getUserById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Отсутствует пользователь с id: " + id);
        }
        checkForEmailDuplicate(userDto);
        User newUser = UserMapper.dtoToUser(userDto);
        User userForUpdate = repository.getUserById(id);
        if (newUser.getName() != null) {
            userForUpdate.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            userForUpdate.setEmail(newUser.getEmail());
        }
        return UserMapper.toUserDto(repository.updateUser(userForUpdate));
    }

    @Override
    public void deleteUser(long id) {
        if (repository.getUserById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Отсутствует пользователь с id: " + id);
        }
        repository.deleteUser(id);
    }

    @Override
    public UserDto getUserById(long id) {
        if (repository.getUserById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Отсутствует пользователь с id: " + id);
        }
        return UserMapper.toUserDto(repository.getUserById(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> repositoryUsers = repository.getAllUsers();
        List<UserDto> refinedUsers = new ArrayList<>();
        for (User repositoryUser : repositoryUsers) {
            refinedUsers.add(UserMapper.toUserDto(repositoryUser));
        }
        return refinedUsers;
    }

    private void checkForEmailDuplicate(UserDto user) {
        List<UserDto> repositoryUsers = getAllUsers();
        for (UserDto repositoryUser : repositoryUsers) {
            if (!Objects.equals(repositoryUser.getId(), user.getId())
                    && repositoryUser.getEmail().equals(user.getEmail())) {
                throw new EmailAlreadyExistsException("Пользователь с таким email уже существует!");
            }
        }
    }
}
