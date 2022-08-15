package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createItemRequest(long requesterId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(requesterId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует пользователь с id: " + requesterId));
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(itemRequestDto, user);
        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestDto getItemRequestById(long userId, long itemRequestId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует пользователь с id: " + userId));
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует запрос с id: " + itemRequestId));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestsByUserId(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует пользователь с id: " + userId));
        List<ItemRequest> itemRequests = itemRequestRepository.findItemRequestsByRequestorOrderByCreatedDesc(user);
        return ItemRequestMapper.itemRequestDtoList(itemRequests);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer from, Integer size) {
        List<ItemRequest> allItemRequests =
                itemRequestRepository.findAll(PageRequest.of(from/size, size))
                        .stream().collect(Collectors.toList());
        return ItemRequestMapper.itemRequestDtoList(allItemRequests);
    }
}
