package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewBookingRequest {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    @NotNull(message = "Id искомой вещи должен быть указан")
    @Positive(message = "Id вещи должен быть больше 0")
    private Long itemId;
    @Positive(message = "Id пользователя, который хочет забронировать вещь, должно быть больше 0")
    private Long bookerId;
}
