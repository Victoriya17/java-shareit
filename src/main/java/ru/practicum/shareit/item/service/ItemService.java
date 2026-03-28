package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.OtherItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(Long userId, NewItemRequest itemRequest);

    ItemDto updateItem(Long itemId, UpdateItemRequest itemRequest, Long ownerId);

    OtherItemDto findItemById(Long ownerId, Long itemId);

    Collection<OtherItemDto> findAll(Long userId);

    Collection<ItemDto> findItemToBooking(Long ownerId, String text);

    CommentDto addComment(Long itemId, Long authorId, NewCommentRequest commentRequest);
}
