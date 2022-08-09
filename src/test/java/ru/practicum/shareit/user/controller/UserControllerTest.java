package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    void getAll() throws Exception {
        List<UserDto> list = new ArrayList<>(1);
        UserDto user = new UserDto();
        user.setId(1L);
        list.add(user);
        Mockito.when(userService.getAllUsers()).thenReturn(list);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(user.getId()), Long.class));
    }

    @Test
    void getUserById() throws Exception {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("name");
        Mockito.when(userService.getUserById(Mockito.eq(user.getId()))).thenReturn(user);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName()), String.class));
    }

    @Test
    void updateUser() throws Exception {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("name");

        UserDto updatedUser = new UserDto();
        updatedUser.setId(1L);
        updatedUser.setName("new name");
        Mockito.when(userService.updateUser(Mockito.eq(updatedUser.getId()), Mockito.any(UserDto.class)))
                .thenReturn(updatedUser);

        mvc.perform(
                    patch("/users/1")
                            .content(mapper.writeValueAsString(updatedUser))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUser.getName()), String.class));
    }

    @Test
    void createUser() throws Exception {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("name");
        user.setEmail("email@email.com");
        Mockito.when(userService.createUser(Mockito.any(UserDto.class))).thenReturn(user);

        mvc.perform(
                post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        Mockito.verify(userService).deleteUser(Mockito.eq(1L));
    }
}