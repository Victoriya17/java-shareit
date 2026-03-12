package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Boolean available;
    private Long requestId;
}
