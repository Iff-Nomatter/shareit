package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto createUser(UserDto userDto) {
        try {
            User user = repository.save(UserMapper.dtoToUser(userDto));
            return UserMapper.toUserDto(user);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Пользователь с email: " +
                    userDto.getEmail() + " уже существует.");
        }
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User user = UserMapper.dtoToUser(getUserById(id));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        repository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Отсутствует пользователь с id: " + id);
        }
    }

    @Override
    public UserDto getUserById(long id) {
        User user = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Отсутствует пользователь с id: " + id));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers(Integer from, Integer size) {
        Page<User> allUsersPage = repository.findAll(PageRequest.of(from / size, size));
        List<User> allUsers = allUsersPage.getContent();
        List<UserDto> allUsersDto = new ArrayList<>();
        for (User user : allUsers) {
            allUsersDto.add(UserMapper.toUserDto(user));
        }
        return allUsersDto;
    }
}
