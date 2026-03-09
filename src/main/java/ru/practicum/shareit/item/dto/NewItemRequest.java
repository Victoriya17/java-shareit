package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class NewItemRequest {
    private Long id;
    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;
    @NotBlank(message = "Описание вещи не может быть пустым")
    private String description;
    @Positive(message = "Id владельца вещи не может быть меньше или равна 0")
    private Long ownerId;
    @NotNull(message = "Доступность вещи должна быть указана")
    private Boolean available;
    @Positive(message = "Id запроса на вещь не может быть меньше или равна 0")
    private Long requestId;
}
