package ru.practicum.shareit.user.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public User create(@Valid @RequestBody UserDto user) {
        return service.add(user);
    }

    @PatchMapping("/{id}")
    public User update(@Valid @RequestBody UserDto user, @PathVariable long id) {
        return service.update(id, user);
    }

    @GetMapping
    public List<User> findAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable long id) {
        return service.getById(id);
    }
}