package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.OtherItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setOwnerId(item.getOwner().getId());
        dto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }
        return dto;
    }

    public static Item mapToItem(User owner, NewItemRequest request) {
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setOwner(owner);
        item.setAvailable(request.getAvailable());
        return item;
    }

    public static Item updateItemFields(Item item, UpdateItemRequest request) {
        if (request.hasName()) {
            item.setName(request.getName());
        }
        if (request.hasDescription()) {
            item.setDescription(request.getDescription());
        }
        if (request.hasAvailable()) {
            item.setAvailable(request.getAvailable());
        }
        return item;
    }

    public static OtherItemDto mapToOtherItemDto(Item item,
                                                 List<Comment> comments,
                                                 Optional<LocalDateTime> lastBooking,
                                                 Optional<LocalDateTime> nextBooking) {
        OtherItemDto dto = new OtherItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwner().getId());
        lastBooking.ifPresent(dto::setLastBooking);
        nextBooking.ifPresent(dto::setNextBooking);
        dto.setComments(comments.stream().map(CommentMapper::mapToCommentDto).toList());
        return dto;
    }

    public static OtherItemDto mapToOtherItemDto(Item item, List<Comment> comments) {
        OtherItemDto dto = new OtherItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwner().getId());
        dto.setComments(comments.stream().map(CommentMapper::mapToCommentDto).toList());
        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }
        return dto;
    }
}
