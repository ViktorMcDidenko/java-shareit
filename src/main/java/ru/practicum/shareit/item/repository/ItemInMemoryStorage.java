//package ru.practicum.shareit.item.repository;
//
//import org.springframework.stereotype.Component;
//import ru.practicum.shareit.item.model.Item;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Component
//public class ItemInMemoryStorage implements ItemStorage {
//    private Map<Long, Item> items = new HashMap<>();
//    private long idGenerator = 1;
//
//    @Override
//    public Item add(Item item) {
//        item.setId(idGenerator++);
//        items.put(item.getId(), item);
//        return items.get(item.getId());
//    }
//
//    @Override
//    public Item update(Item item) {
//        items.put(item.getId(), item);
//        return items.get(item.getId());
//    }
//
//    @Override
//    public Item getById(Long id) {
//        return items.get(id);
//    }
//
//    @Override
//    public List<Item> get(long userId) {
//        return items.values().stream().filter(i -> i.getOwner() == userId).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<Item> search(String text) {
//        return items.values().stream()
//                .filter(i -> i.getAvailable().equals(true)
//                        && (i.getName().toLowerCase().contains(text.toLowerCase())
//                        || i.getDescription().toLowerCase().contains(text.toLowerCase())))
//                .collect(Collectors.toList());
//    }
//}