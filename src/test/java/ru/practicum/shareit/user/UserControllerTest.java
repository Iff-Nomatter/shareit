package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ru.practicum.shareit.user.dto.UserDto;


import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class UserControllerTest {

    private static final String ADDRESS = "http://localhost:";
    private static final String ENDPOINT = "/users";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;


    static UserDto user;
    static UserDto userForDeletion;
    static UserDto userForRequest;
    static UserDto userForCreation;
    static UserDto userForGetAll;
    static UserDto userForDuplicateEmail;
    static UserDto updatedUser;
    static UserDto badUser;
    static URI url;

    @BeforeEach
    void init() {
        url = URI.create(ADDRESS + port + ENDPOINT);
        user = new UserDto(0, "Frodo", "hobbit@shire.nz");
        userForRequest = new UserDto(0, "Gandalf", "mayar@shire.nz");
        userForGetAll = new UserDto(0, "Aragorn", "mitrandir@pony.nz");
        userForDeletion = new UserDto(0, "Sam", "gardener@shire.kz");
        userForDuplicateEmail = new UserDto(0, "Merry", "meriadok@shire.kz");
        userForCreation = new UserDto(0, "Peregrin", "tuk@shire.kz");
        updatedUser = new UserDto(1L, "Mr.Frodo", "");
        badUser = new UserDto(15L, "", "w00t");
    }

    @Test
    void getAll() {
        template.postForObject(url, userForGetAll, UserDto.class);
        assertThat(this.template.getForObject(url, List.class)).isNotEmpty();
    }

    @Test
    void getUserById() {
        ResponseEntity<UserDto> creation = template.postForEntity(url, userForRequest, UserDto.class);
        UserDto createdUser = creation.getBody();
        ResponseEntity<UserDto> response = template.getForEntity(url + "/" + createdUser.getId(), UserDto.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateUser() {
        template.postForObject(url, user, UserDto.class);
        ResponseEntity<UserDto> response = template.exchange(url + "/1", HttpMethod.PATCH,
                new HttpEntity<>(updatedUser), UserDto.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createUser() {
        ResponseEntity<UserDto> response = template.postForEntity(url, userForCreation, UserDto.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteUser() {
        ResponseEntity<UserDto> response = template.postForEntity(url, userForDeletion, UserDto.class);
        UserDto responseDto = response.getBody();
        template.delete(url + "/" + responseDto.getId());
        ResponseEntity<UserDto> notFoundResponse = template.getForEntity(url + "/" + responseDto.getId(),
                UserDto.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
    }

    @Test
    void badUser() {
        ResponseEntity<UserDto> response = template.postForEntity(url, badUser, UserDto.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void duplicateEmail() {
        template.postForObject(url, userForDuplicateEmail, UserDto.class);
        ResponseEntity<UserDto> response = template.postForEntity(url, userForDuplicateEmail, UserDto.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}