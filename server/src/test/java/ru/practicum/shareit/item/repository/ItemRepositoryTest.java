package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void searchByNameAndDescriptionAndAvailableName() {
        User user = new User();
        user.setName("user");
        user.setEmail("email@email.com");
        userRepository.saveAndFlush(user);

        Item item = new Item();
        item.setName("testtext");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.saveAndFlush(item);

        List<Item> result = itemRepository.searchByNameAndDescriptionAndAvailable("test", PageRequest.ofSize(10));
        assertEquals(1, result.size());
    }

    @Test
    void searchByNameAndDescriptionAndAvailableDescription() {
        User user = new User();
        user.setName("user");
        user.setEmail("email@email.com");
        userRepository.saveAndFlush(user);

        Item item = new Item();
        item.setName("text");
        item.setDescription("testdesc");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.saveAndFlush(item);

        List<Item> result = itemRepository.searchByNameAndDescriptionAndAvailable("test", PageRequest.ofSize(10));
        assertEquals(1, result.size());
    }

    @Test
    void searchByNameAndDescriptionAndAvailableNotAvailable() {
        User user = new User();
        user.setName("user");
        user.setEmail("email@email.com");
        userRepository.saveAndFlush(user);

        Item item = new Item();
        item.setName("text");
        item.setDescription("testdesc");
        item.setAvailable(false);
        item.setOwner(user);
        itemRepository.saveAndFlush(item);

        List<Item> result = itemRepository.searchByNameAndDescriptionAndAvailable("test", PageRequest.ofSize(10));
        assertEquals(0, result.size());
    }

    @Test
    void searchByNameAndDescriptionAndAvailableDoNotContainText() {
        User user = new User();
        user.setName("user");
        user.setEmail("email@email.com");
        userRepository.saveAndFlush(user);

        Item item = new Item();
        item.setName("text");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.saveAndFlush(item);

        List<Item> result = itemRepository.searchByNameAndDescriptionAndAvailable("test", PageRequest.ofSize(10));
        assertEquals(0, result.size());
    }
}