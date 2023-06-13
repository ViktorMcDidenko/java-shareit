package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(UserDto user);

    UserDto update(long id, UserDto user);

    List<UserDto> getAll();

    UserDto getById(long id);

    void delete(long id);
}