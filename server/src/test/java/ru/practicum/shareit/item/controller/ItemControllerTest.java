package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    void createItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        Long userId = 111L;
        Mockito.when(itemService.createItem(eq(userId), any(ItemDto.class))).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(1L);
        updatedItemDto.setName("updated name");
        updatedItemDto.setDescription("updated description");
        updatedItemDto.setAvailable(true);
        Long userId = 111L;
        Mockito.when(itemService.updateItem(eq(userId), any(ItemDto.class), eq(updatedItemDto.getId())))
                .thenReturn(updatedItemDto);

        mvc.perform(
                        post("/items/1")
                                .content(mapper.writeValueAsString(updatedItemDto))
                                .header("X-Sharer-User-Id", userId)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItemDto.getName()), String.class));
    }

    @Test
    void getItemById() throws Exception {
        ItemDtoForOwner itemDto = new ItemDtoForOwner();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        Long userId = 111L;
        Mockito.when(itemService.getItemById(eq(itemDto.getId()), eq(userId))).thenReturn(itemDto);

        mvc.perform(
                        get("/items/1")
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class));
    }

    @Test
    void getAllItemsByUserId() throws Exception {
        ItemDtoForOwner itemDto = new ItemDtoForOwner();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        Long userId = 111L;
        Mockito.when(itemService.getAllItemsByUserId(eq(userId), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(itemDto));

        mvc.perform(
                        get("/items?from=0&size=5")
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class));
    }

    @Test
    void searchItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        Long userId = 111L;
        Mockito.when(itemService.searchItem(anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(itemDto));

        mvc.perform(
                        get("/items/search?text=anytext&from=0&size=5")
                                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class));
    }

    @Test
    void postComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("comment text");
        Long userId = 111L;
        Long itemId = 222L;
        Mockito.when(itemService.postComment(eq(userId), eq(itemId), any(CommentDto.class))).thenReturn(commentDto);

        mvc.perform(
                        post("/items/222/comment")
                                .content(mapper.writeValueAsString(commentDto))
                                .header("X-Sharer-User-Id", userId)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class));
    }
}
