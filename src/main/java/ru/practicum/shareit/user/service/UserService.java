package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@SuppressWarnings("checkstyle:Regexp")
public interface UserService {
    User add(UserDto user);

    User update(long id, UserDto user);

    List<User> getAll();

    User getById(long id);

    void delete(long id);

    void checkId(long id);
}