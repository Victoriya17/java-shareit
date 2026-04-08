package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItem(Item updatedItem);

    Optional<Item> findItemById(Long itemId);

    Collection<Item> findAllItems(Long userId);

    Collection<Item> findItemToBooking(String text);
}
