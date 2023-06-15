package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public UserDto add(UserDto user) {
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(user)));
    }

    @Override
    @Transactional
    public UserDto update(long id, UserDto user) {
        UserDto savedUser = getById(id);
        if (user.getEmail() != null) {
            savedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            savedUser.setName(user.getName());
        }
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(savedUser)));
    }

    @Override
    public List<UserDto> getAll() {
        return UserMapper.toDtoList(repository.findAll()); //finall нежелателен
    }

    @Override
    public UserDto getById(long id) {
        return UserMapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("There is no user with id %d.", id))));
    }

    @Override
    @Transactional
    public void delete(long id) {
        repository.deleteById(id);
    }
}