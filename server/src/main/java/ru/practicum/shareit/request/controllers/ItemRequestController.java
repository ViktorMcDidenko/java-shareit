package ru.practicum.shareit.request.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") long requestorId,
                              @RequestBody ItemRequestDto itemRequestDto) {
        return service.add(requestorId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getYours(@RequestHeader("X-Sharer-User-Id") long id) {
        return service.getYours(id);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getTheirs(@RequestHeader("X-Sharer-User-Id") long id,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return service.getTheirs(id, pageable);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return service.getById(userId, requestId);
    }
}