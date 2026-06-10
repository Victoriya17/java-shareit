package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody NewRequest request) {
        return itemRequestService.createItemRequest(userId, request);
    }

    @GetMapping
    public Collection<ItemRequestDto> findAllItemRequestsByRequestorId(@RequestHeader("X-Sharer-User-Id")
                                                                       Long requestorId) {
        return itemRequestService.findAllItemRequestsByRequestorId(requestorId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> findAllItemRequestsCreatedByOtherUsers(@RequestHeader("X-Sharer-User-Id")
                                                                             Long requestorId) {
        return itemRequestService.findAllItemRequests(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findRequestById(@PathVariable Long requestId) {
        return itemRequestService.findRequestById(requestId);
    }
}
