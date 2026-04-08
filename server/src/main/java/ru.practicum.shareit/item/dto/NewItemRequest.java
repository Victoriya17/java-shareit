package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class NewItemRequest {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Boolean available;
    private Long requestId;
}
