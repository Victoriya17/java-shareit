package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, NewRequest request);

    Collection<ItemRequestDto> findAllItemRequestsByRequestorId(Long requestorId);

    Collection<ItemRequestDto> findAllItemRequests(Long requestorId);

    ItemRequestDto findRequestById(Long requestId);
}
