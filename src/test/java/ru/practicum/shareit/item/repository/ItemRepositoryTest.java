package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void search() {
        User user = new User("user", "mail@gmail.com");
        Item item1 = new Item(user, "ПоИсК по названию", "description1");
        item1.setAvailable(true);
        Item item2 = new Item(user, "вещь2", "По описанию поиск");
        item2.setAvailable(true);
        Item item3 = new Item(user, "поиск в названии", "поиск в описании");
        item3.setAvailable(false);

        em.persistAndFlush(user);
        repository.save(item1);
        repository.save(item2);
        repository.save(item3);

        List<Item> result = repository.search("поиск", PageRequest.of(0, 10));

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
    }
}