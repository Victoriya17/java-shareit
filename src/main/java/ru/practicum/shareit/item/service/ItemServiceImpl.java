package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotItemOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements  ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(Long userId, NewItemRequest itemRequest) {
        log.debug("Создание записи о новой вещи {}", itemRequest.getName());
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = ItemMapper.mapToItem(userId, itemRequest);
        item = itemStorage.createItem(item);

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, UpdateItemRequest itemRequest, Long ownerId) {
        Item existingItem = itemStorage.findItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        User user = userStorage.findUserById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new NotItemOwnerException("Редактировать данные вещи может только её владелец");
        }

        Item updatedItem = ItemMapper.updateItemFields(existingItem, itemRequest);
        updatedItem = itemStorage.updateItem(updatedItem);

        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        log.debug("Поиск вещи c ID{}", itemId);
        Item item = itemStorage.findItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение списка всех вещей пользователя");
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return itemStorage.findAllItems(userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> findItemToBooking(Long ownerId, String text) {
        log.debug("Получаем записи о вещах, которые ищет арендатор по ключевым символам {}", text);
        User user = userStorage.findUserById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemStorage.findItemToBooking(text.toLowerCase())
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
