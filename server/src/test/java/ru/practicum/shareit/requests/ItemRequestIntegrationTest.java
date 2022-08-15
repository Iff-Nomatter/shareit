package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ItemRequestIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void createItemRequest() {
        User user = new User();
        user.setName("requestor");
        user.setEmail("requestor@email.com");
        userRepository.saveAndFlush(user);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");
        ItemRequestDto result = itemRequestService.createItemRequest(user.getId(), itemRequestDto);

        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(result.getId());
        assertTrue(itemRequest.isPresent());
        assertEquals(itemRequestDto.getDescription(), itemRequest.get().getDescription());
    }

    @Test
    void getItemRequestById() {
        User user = new User();
        user.setName("requestor1");
        user.setEmail("requestor1@email.com");
        userRepository.saveAndFlush(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setDescription("Get this item request");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.saveAndFlush(itemRequest);

        ItemRequestDto dto = itemRequestService.getItemRequestById(user.getId(), itemRequest.getId());
        assertEquals(dto.getDescription(), itemRequest.getDescription());
    }
}
