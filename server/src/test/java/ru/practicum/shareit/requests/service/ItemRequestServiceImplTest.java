package ru.practicum.shareit.requests.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @Mock
    private UserRepository mockUserRepository;

    @Test
    void createItemRequest() {
        User user = new User();
        user.setId(1);
        user.setName("User1");
        user.setEmail("User1@email.com");
        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        Mockito.when(mockItemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenAnswer((Answer<ItemRequest>) invocationOnMock -> {
                    ItemRequest savedItemRequest = invocationOnMock.getArgument(0);
                    savedItemRequest.setId(1);
                    return savedItemRequest;
                });

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");
        ItemRequestDto result = itemRequestService.createItemRequest(1L, itemRequestDto);
        assertEquals(1L, result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
    }

    @Test
    void createItemRequestEmptyUser() {
        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,
                () -> itemRequestService.createItemRequest(1L, new ItemRequestDto()));
    }

    @Test
    void getItemRequestById() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("description");
        User user = new User();
        user.setId(1);
        itemRequest.setRequestor(user);
        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(mockItemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequestDto result = itemRequestService.getItemRequestById(1L, 1L);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
    }


    @Test
    void getAllItemRequestsByUserId() {
        User user = new User();
        user.setId(1);
        user.setName("User1");
        user.setEmail("User1@email.com");
        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        ItemRequest itemRequestOne = new ItemRequest();
        itemRequestOne.setId(1);
        itemRequestOne.setDescription("description1");
        itemRequestOne.setRequestor(user);

        ItemRequest itemRequestTwo = new ItemRequest();
        itemRequestTwo.setId(2);
        itemRequestTwo.setDescription("description2");
        itemRequestTwo.setRequestor(user);

        List<ItemRequest> requestsFromDB = new ArrayList<>();
        requestsFromDB.add(itemRequestOne);
        requestsFromDB.add(itemRequestTwo);
        Mockito.when(mockItemRequestRepository.findItemRequestsByRequestorOrderByCreatedDesc(
                Mockito.any())
        ).thenReturn(requestsFromDB);

        List<ItemRequestDto> result = itemRequestService.getAllItemRequestsByUserId(1L);
        assertEquals(2, result.size());
    }

    @Test
    void getAllItemRequestsByUserIdUserNotFound() {
        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,
                () -> itemRequestService.getAllItemRequestsByUserId(1L));
    }

    @Test
    void getAllItemRequests() {
        User user = new User();
        user.setId(1);

        ItemRequest itemRequestOne = new ItemRequest();
        itemRequestOne.setId(1);
        itemRequestOne.setDescription("description1");
        itemRequestOne.setRequestor(user);

        ItemRequest itemRequestTwo = new ItemRequest();
        itemRequestTwo.setId(2);
        itemRequestTwo.setDescription("description2");
        itemRequestTwo.setRequestor(user);

        List<ItemRequest> requestsFromDB = new ArrayList<>();
        requestsFromDB.add(itemRequestOne);
        requestsFromDB.add(itemRequestTwo);
        Page<ItemRequest> page = new PageImpl<>(requestsFromDB);
        Mockito.when(mockItemRequestRepository.findAll(
                Mockito.any(PageRequest.class))
        ).thenReturn(page);

        List<ItemRequestDto> result = itemRequestService.getAllItemRequests(2,0, 5);
        assertEquals(2, result.size());
    }
}