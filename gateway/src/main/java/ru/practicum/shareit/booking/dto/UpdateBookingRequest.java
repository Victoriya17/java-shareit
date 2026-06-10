package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.enums.Status;

import java.time.LocalDateTime;

@Data
public class UpdateBookingRequest {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private Status status;

    public boolean hasStart() {
        return !(start == null);
    }

    public boolean hasEnd() {
        return !(end == null);
    }
}

