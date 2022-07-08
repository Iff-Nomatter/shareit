package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 0;

    @Override
    public User createUser(User user) {
        user.setId(generateUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private long generateUserId() {
        idCounter++;
        return idCounter;
    }
}
