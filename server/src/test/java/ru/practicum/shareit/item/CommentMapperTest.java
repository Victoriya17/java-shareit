package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {
    private User author;
    private Item item;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        author.setName("Ivan");

        item = new Item();
        item.setId(10L);
    }

    @Test
    void shouldMapToCommentDto() {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(now);

        CommentDto dto = CommentMapper.mapToCommentDto(comment);

        assertNotNull(dto);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(author.getName(), dto.getAuthorName());
        assertEquals(item.getId(), dto.getItemId());
        assertEquals(now, dto.getCreated());
    }

    @Test
    void shouldMapToComment() {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("New feedback");

        Comment comment = CommentMapper.mapToComment(author, item, request);

        assertNotNull(comment);
        assertEquals(request.getText(), comment.getText());
        assertEquals(author, comment.getAuthor());
        assertEquals(item, comment.getItem());
        assertNotNull(comment.getCreated());
        assertTrue(comment.getCreated().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}