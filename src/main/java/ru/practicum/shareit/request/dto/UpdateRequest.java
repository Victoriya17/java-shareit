package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateRequest {
    @NotNull(message = "Id запроса вещи должен быть указан")
    private Long id;
    private String description;
    private Long requestorId;
    private LocalDateTime created;
}
