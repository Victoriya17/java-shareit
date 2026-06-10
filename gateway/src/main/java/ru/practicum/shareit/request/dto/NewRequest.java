package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewRequest {
    @NotBlank(message = "Запрос не может быть пустым")
    private String description;
    private Long requestorId;
}
