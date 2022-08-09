package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testEmail() throws IOException {
        UserDto userDto = new UserDto();
        userDto.setName("user");
        userDto.setEmail("email@email.com");
        JsonContent<UserDto> result = this.json.write(userDto);

        assertThat(result).hasJsonPathStringValue("$.email");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("email@email.com");
    }

}