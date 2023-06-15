package ru.practicum.shareit.user.model;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static User toUser(UserDto dto) {
        return new User(dto.getId(), dto.getName(), dto.getEmail());
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static List<UserDto> toDtoList(List<User> l) {
        return l.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}