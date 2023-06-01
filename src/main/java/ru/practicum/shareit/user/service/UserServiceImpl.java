package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage repository;
    private final UserMapper mapper;

    @Override
    public User add(UserDto user) {
        checkEmail(user.getEmail());
        return repository.add(mapper.toUser(user));
    }

    @Override
    public User update(long id, UserDto user) {
        checkId(id);
        User savedUser = getById(id);
        if (user.getEmail() == null) {
            user.setEmail(savedUser.getEmail());
        }
        if (user.getName() == null) {
            if (!user.getEmail().equals(savedUser.getEmail())) {
                checkEmail(user.getEmail());
            }
            user.setName(savedUser.getName());
        }
        return repository.update(id, mapper.toUser(user));
    }

    @Override
    public List<User> getAll() {
        return repository.getAll();
    }

    @Override
    public User getById(long id) {
        return repository.getById(id);
    }

    @Override
    public void delete(long id) {
        repository.delete(id);
    }

    @Override
    public void checkId(long id) {
        boolean result = getAll().stream().anyMatch(u -> u.getId() == id);
        if (!result) {
            throw new NotFoundException(String.format("There is no user with id %d.", id));
        }
    }

    private void checkEmail(String email) {
        boolean result = getAll().stream().noneMatch(user -> user.getEmail().equals(email));
        if (!result) {
            throw new ValidationException(String.format("User with email %s already exists.", email));
        }
    }
}