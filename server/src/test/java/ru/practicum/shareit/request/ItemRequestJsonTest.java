package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> requestJson;

    @Test
    void testItemRequestDto() throws Exception {
        LocalDateTime created = LocalDateTime.of(2026, 4, 8, 12, 0, 0);

        ItemDto responseDto = new ItemDto(1L, "name", "description", 1L, true, 1L);
        List<ItemDto> items = List.of(responseDto);

        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", 1L, created, items);

        JsonContent<ItemRequestDto> result = requestJson.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-04-08T12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("name");
    }
}