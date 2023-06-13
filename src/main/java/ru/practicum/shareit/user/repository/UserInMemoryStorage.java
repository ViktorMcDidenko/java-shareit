package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserInMemoryStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();
    private long idGenerator = 1;

    @Override
    public User add(User user) {
        user.setId(idGenerator++);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(long id) {
        return users.get(id);
    }

    @Override
    public User update(long id, User user) {
        user.setId(id);
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }
}