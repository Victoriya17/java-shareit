package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OtherItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemJsonTest {

    @Autowired
    private JacksonTester<ItemDto> itemJson;

    @Autowired
    private JacksonTester<OtherItemDto> otherItemJson;

    @Autowired
    private JacksonTester<CommentDto> commentJson;

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "name", "description", 2L, true, 3L);

        JsonContent<ItemDto> result = itemJson.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(3);
    }

    @Test
    void testOtherItemDto() throws Exception {
        LocalDateTime last = LocalDateTime.of(2025, 5, 3, 18, 15, 12);
        LocalDateTime next = LocalDateTime.of(2025, 5, 5, 17, 20, 15);
        LocalDateTime commentTime = LocalDateTime.of(2025, 5, 4, 10, 0, 0);

        CommentDto comment = new CommentDto(1L, "text", 2L, "Ivan", commentTime);

        OtherItemDto otherItemDto = new OtherItemDto(
                1L, "item name", "item desc", 10L, true, 100L,
                last, next, List.of(comment)
        );

        JsonContent<OtherItemDto> result = otherItemJson.write(otherItemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item name");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isEqualTo("2025-05-03T18:15:12");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isEqualTo("2025-05-05T17:20:15");
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("Ivan");
    }

    @Test
    void testCommentDto() throws Exception {
        LocalDateTime created = LocalDateTime.of(2025, 6, 1, 12, 0, 0);
        CommentDto commentDto = new CommentDto(5L, "comment text", 1L, "Author", created);

        JsonContent<CommentDto> result = commentJson.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment text");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-06-01T12:00:00");
    }
}
