package ru.practicum.shareit.user.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserMapper {
    public User toUser(UserDto dto) {
        return new User(dto.getName(), dto.getEmail());
    }

    public UserDto toUserDto(User user) {
        return new UserDto(user.getName(), user.getEmail());
    }
}