package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    String name;
    String description;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long ownerId;
    Boolean available;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long requestId;
}
