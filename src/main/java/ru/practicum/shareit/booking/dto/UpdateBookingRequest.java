package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.enums.Statuses;

import java.time.LocalDateTime;

@Data
public class UpdateBookingRequest {
    @NotNull(message = "Id бронирования должен быть указан")
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
    Long bookerId;
    Statuses status;

    public boolean hasStart() {
        return !(start == null);
    }

    public boolean hasEnd() {
        return !(end == null);
    }
}
