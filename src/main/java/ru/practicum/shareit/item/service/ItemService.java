package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(Long userId, NewItemRequest itemRequest);

    ItemDto updateItem(Long itemId, UpdateItemRequest itemRequest, Long ownerId);

    ItemDto findItemById(Long itemId);

    Collection<ItemDto> findAll(Long userId);

    Collection<ItemDto> findItemToBooking(Long ownerId, String text);
}
