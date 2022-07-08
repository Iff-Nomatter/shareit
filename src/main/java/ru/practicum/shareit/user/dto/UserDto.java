package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotEmpty(message = "Field 'name' must not be empty")
    private String name;
    @Email(message = "Field 'email' must be valid email")
    @NotEmpty(message = "Field 'email' must not be empty")
    private String email;
}
