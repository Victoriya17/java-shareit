package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.enums.Status;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Status status;
    private Long bookerId;
}
