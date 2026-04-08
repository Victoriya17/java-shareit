package ru.practicum.shareit.item.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Component("InMemoryItemStorage")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Item updateItem(Item updatedItem) {
        log.info("Начало обновления вещи. ID: {}", updatedItem.getId());
        items.put(updatedItem.getId(), updatedItem);
        return updatedItem;
    }

    @Override
    public Optional<Item> findItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    private boolean checkItem(Item item, String text) {
        return item.getAvailable().equals(Boolean.TRUE)
                && (item.getName().toLowerCase().contains(text)
                || item.getDescription().toLowerCase().contains(text));
    }

    @Override
    public Collection<Item> findAllItems(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public Collection<Item> findItemToBooking(String text) {
        return items.values()
                .stream()
                .filter(item -> checkItem(item, text))
                .toList();
    }
}
