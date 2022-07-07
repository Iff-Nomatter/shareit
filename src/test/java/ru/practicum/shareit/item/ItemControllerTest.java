package ru.practicum.shareit.item;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.net.URI;
import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class ItemControllerTest {

    private static final String ADDRESS = "http://localhost:";
    private static final String ENDPOINT = "/items";


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;
    @Autowired
    private UserRepository userRepository;



    static ItemDto item;
    static ItemDto itemForCreation;
    static ItemDto itemForRequest;
    static ItemDto updatedItem;
    static URI url;

    @BeforeAll
    void fillUserBase() {
        User user1 = new User();
        user1.setId(0);
        user1.setName("user1");
        user1.setEmail("user1@user.use");
        userRepository.createUser(user1);
    }

    @BeforeEach
    void init() {
        url = URI.create(ADDRESS + port + ENDPOINT);
        item = new ItemDto(0, "Посох", "Деревянный", true);
        updatedItem = new ItemDto(1L, "Посох", "Из белого дерева", false);
        itemForCreation = new ItemDto(0, "Дневник Бильбо", "Ручная работа", true);
        itemForRequest = new ItemDto(0, "штука", "штучная работа", true);
    }

    @Test
    void createItem() {
        HttpHeaders user1Header = new HttpHeaders();
        user1Header.add("X-Sharer-User-Id", "1");
        HttpEntity<ItemDto> itemRequest = new HttpEntity<>(item, user1Header);
        ResponseEntity<ItemDto> response = template.exchange(url,
                HttpMethod.POST,
                itemRequest,
                ItemDto.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateItem() {
        HttpHeaders user1Header = new HttpHeaders();
        user1Header.add("X-Sharer-User-Id", "1");
        HttpEntity<ItemDto> itemRequest = new HttpEntity<>(item, user1Header);
        ResponseEntity<ItemDto> response = template.exchange(url,
                HttpMethod.POST,
                itemRequest,
                ItemDto.class);
        ItemDto itemDtoResponse = response.getBody();
        HttpEntity<ItemDto> itemUpdateRequest = new HttpEntity<>(updatedItem, user1Header);
        ResponseEntity<ItemDto> updateResponse = template.exchange(url + "/" + itemDtoResponse.getId(),
                HttpMethod.PATCH,
                itemUpdateRequest,
                ItemDto.class);
        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    }

    @Test
    void getItemById() {
        HttpHeaders user1Header = new HttpHeaders();
        user1Header.add("X-Sharer-User-Id", "1");
        HttpEntity<ItemDto> itemRequest = new HttpEntity<>(itemForRequest, user1Header);
        ResponseEntity<ItemDto> response = template.exchange(url,
                HttpMethod.POST,
                itemRequest,
                ItemDto.class);
        ItemDto itemDtoResponse = response.getBody();
        ResponseEntity<ItemDto> getByIdResponse = template.exchange(url + "/" + itemDtoResponse.getId(),
                HttpMethod.GET,
                itemRequest,
                ItemDto.class);
        Assertions.assertEquals(HttpStatus.OK, getByIdResponse.getStatusCode());
    }

    @Test
    void getAllItemsByUserId() {
        HttpHeaders user1Header = new HttpHeaders();
        user1Header.add("X-Sharer-User-Id", "1");
        HttpEntity<ItemDto> item1Request = new HttpEntity<>(item, user1Header);
        template.exchange(url,
                HttpMethod.POST,
                item1Request,
                ItemDto.class);
        HttpEntity<ItemDto> item2Request = new HttpEntity<>(itemForCreation, user1Header);
        template.exchange(url,
                HttpMethod.POST,
                item2Request,
                ItemDto.class);
        ResponseEntity<List> getItemsByOwnerId = template.exchange(url, HttpMethod.GET, item2Request, List.class);
        List fetchedItems = getItemsByOwnerId.getBody();
        assert fetchedItems != null;
    }

    @Test
    void searchItem() {
        HttpHeaders user1Header = new HttpHeaders();
        user1Header.add("X-Sharer-User-Id", "1");
        HttpEntity<ItemDto> item1Request = new HttpEntity<>(item, user1Header);
        template.exchange(url,
                HttpMethod.POST,
                item1Request,
                ItemDto.class);
        HttpEntity<ItemDto> item2Request = new HttpEntity<>(itemForCreation, user1Header);
        template.exchange(url,
                HttpMethod.POST,
                item2Request,
                ItemDto.class);
        ResponseEntity<List> getItemsBySearch = template.exchange(url + "/search?text=осо", HttpMethod.GET, item2Request, List.class);
        List fetchedItems = getItemsBySearch.getBody();
        assert fetchedItems != null;
    }
}