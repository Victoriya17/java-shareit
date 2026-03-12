package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewRequest {
    private Long id;
    @NotBlank(message = "Запрос не может быть пустым")
    private String description;
    @NotNull(message = "Id пользователя, который оставил запрос, должно быть указано")
    @Positive(message = "Id пользователя, который оставил запрос, должно быть больше 0")
    private Long requestorId;
    private LocalDateTime created;
}
