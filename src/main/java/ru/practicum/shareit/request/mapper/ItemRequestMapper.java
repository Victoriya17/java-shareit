package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setRequestorId(itemRequest.getRequestor().getId());
        dto.setCreated(itemRequest.getCreated());
        return dto;
    }

    public static ItemRequest mapToItemRequest(NewRequest request, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(request.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(request.getCreated());
        return itemRequest;
    }

    public static ItemRequest updateItemRequestFields(ItemRequest itemRequest, UpdateRequest request) {
        itemRequest.setDescription(request.getDescription());
        return itemRequest;
    }
}
