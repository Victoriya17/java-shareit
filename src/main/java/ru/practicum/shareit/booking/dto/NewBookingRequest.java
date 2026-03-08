package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.practicum.shareit.enums.Statuses;

import java.time.LocalDateTime;

@Data
public class NewBookingRequest {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    @NotNull(message = "Id искомой вещи должен быть указан")
    @Positive(message = "Id вещи должен быть больше 0")
    Long itemId;
    @NotNull(message = "Статус бронирования вещи должен быть указан. Укажите одно из: \"WAITING\", \"APPROVED\", " +
            "\"REJECTED\", \"CANCELED\".")
    @NotNull(message = "Id пользователя, который хочет забронировать вещь, должно быть указано")
    @Positive(message = "Id пользователя, который хочет забронировать вещь, должно быть больше 0")
    Long bookerId;
    Statuses status;
}
