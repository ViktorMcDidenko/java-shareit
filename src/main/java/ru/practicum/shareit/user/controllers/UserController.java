package ru.practicum.shareit.user.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.user.dto.UserDto;
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
    @Validated({Create.class})
    public UserDto create(@Valid @RequestBody UserDto user) {
        return service.add(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Valid @RequestBody UserDto user, @PathVariable long id) {
        return service.update(id, user);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        return service.getById(id);
    }
}