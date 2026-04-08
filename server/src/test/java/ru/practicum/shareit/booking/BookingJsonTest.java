package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingJsonTest {
    @Autowired
    private JacksonTester<BookingDto> bookingJson;

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 12, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 2, 10, 0, 0);

        ItemDto itemDto = new ItemDto(1L, "name", "description", 2L, true, 1L);
        UserDto userDto = new UserDto(1L, "Ivan", "ivan@email.com");

        BookingDto bookingDto = new BookingDto(1L, start, end, itemDto, Status.APPROVED, userDto);

        JsonContent<BookingDto> result = bookingJson.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-12-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-12-02T10:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("name");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("ivan@email.com");
    }
}
