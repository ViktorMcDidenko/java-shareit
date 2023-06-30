package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Generated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Generated
public class UserDto {
    private Long id;
    @NotBlank(groups = Create.class)
    private String name;
    @NotBlank(groups = Create.class)
    @Email
    private String email;
}