package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String description;
    private Long requestorId;
    private LocalDateTime created;
}
