package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper mapper;

    @Override
    public ItemRequestDto add(long requestorId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException(String.format("There is no user with id %d.", requestorId)));
        return mapper.toDto(itemRequestRepository.save(mapper.toItemRequest(itemRequestDto, requestor)));
    }

    @Override
    public List<ItemRequestDto> getYours(long id) {
        checkUser(id);
        return mapper.toList(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(id));
    }

    @Override
    public List<ItemRequestDto> getTheirs(long id, int from, int size) {
        checkUser(id);
        return mapper.toList(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(id,
                PageRequest.of(from / size, size)));
    }

    @Override
    public ItemRequestDto getById(long userId, long requestId) {
        checkUser(userId);
        return mapper.toDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("There is no request with id %d.", requestId))));
    }

    private void checkUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("There is no user with id %d.", id));
        }
    }
}