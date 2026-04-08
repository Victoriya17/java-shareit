package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OtherItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private final String url = "/items";
    private final String headerUserId = "X-Sharer-User-Id";

    private ItemDto itemDto;
    private OtherItemDto otherItemDto;
    private CommentDto commentDto;
    private CommentDto commentDto2;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(
                        new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(mapper))
                .build();

        itemDto = new ItemDto(1L, "name", "description", 1L, Boolean.TRUE, 1L);
        commentDto = new CommentDto(1L, "text", 1L, "Ivan",
                LocalDateTime.of(2025, 5, 3, 18, 15, 12));
        commentDto2 = new CommentDto(2L, "text", 1L, "Maria",
                LocalDateTime.of(2025, 6, 3, 18, 15, 12));
        otherItemDto = new OtherItemDto(2L, "name", "description", 1L, Boolean.TRUE, 2L,
                LocalDateTime.of(2025, 5, 3, 18, 15, 12),
                LocalDateTime.of(2025, 5, 9, 18, 15, 12), List.of(commentDto, commentDto2));
    }

    @Test
    void createTest() throws Exception {
        Map<String, Object> newItemRequest = Map.of(
                "name", "name",
                "description", "description",
                "ownerId", itemDto.getOwnerId(),
                "available", Boolean.TRUE,
                "requestId", 1L
        );

        when(itemService.createItem(anyLong(), any())).thenReturn(itemDto);

        mvc.perform(post(url)
                        .header(headerUserId, 1L)
                        .content(mapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void updateTest() throws Exception {
        Map<String, Object> updateItemRequest = Map.of(
                "name", "bag",
                "description", "leather bag",
                "ownerId", itemDto.getOwnerId(),
                "available", Boolean.TRUE,
                "requestId", 1L
        );

        when(itemService.updateItem(anyLong(), any(), anyLong())).thenReturn(itemDto);

        mvc.perform(patch(url + "/" + itemDto.getId())
                        .header(headerUserId, 1L)
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    void findItemTest() throws Exception {
        when(itemService.findItemById(anyLong(), anyLong())).thenReturn(otherItemDto);

        mvc.perform(get(url + "/" + otherItemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(otherItemDto)))
                .andExpect(jsonPath("$.id").value(otherItemDto.getId()))
                .andExpect(jsonPath("$.name").value(otherItemDto.getName()))
                .andExpect(jsonPath("$.description").value(otherItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(otherItemDto.getAvailable()))
                .andExpect(jsonPath("$.ownerId").value(otherItemDto.getOwnerId()))
                .andExpect(jsonPath("$.requestId").value(otherItemDto.getRequestId()))
                .andExpect(jsonPath("$.lastBooking").exists())
                .andExpect(jsonPath("$.nextBooking").exists())
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments.length()").value(2))
                .andExpect(jsonPath("$.comments[0].id").value(commentDto.getId()))
                .andExpect(jsonPath("$.comments[0].authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.comments[1].id").value(commentDto2.getId()));
    }

    @Test
    void findAllBookingsByUserTest() throws Exception {
        CommentDto commentDto3 = new CommentDto(3L, "text", 1L, "Maria",
                LocalDateTime.of(2025, 6, 3, 18, 15, 12));
        OtherItemDto otherItemDto2 = new OtherItemDto(3L, "name", "description", 1L, Boolean.TRUE, 3L,
                LocalDateTime.of(2025, 7, 3, 18, 15, 12),
                LocalDateTime.of(2025, 8, 9, 18, 15, 12), List.of(commentDto3));
        Collection<OtherItemDto> items = List.of(otherItemDto, otherItemDto2);

        when(itemService.findAll(anyLong()))
                .thenReturn(items);

        mvc.perform(get(url)
                        .header(headerUserId, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(content().json(mapper.writeValueAsString(items)))
                .andExpect(jsonPath("$[0].id").value(otherItemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(otherItemDto.getName()))
                .andExpect(jsonPath("$[1].id").value(otherItemDto2.getId()))
                .andExpect(jsonPath("$[1].name").value(otherItemDto2.getName()));
    }

    @Test
    void searchItemsTest() throws Exception {
        ItemDto itemDto2 = new ItemDto(2L, "name", "description", 1L, Boolean.TRUE, 2L);
        Collection<ItemDto> items = List.of(itemDto, itemDto2);
        when(itemService.findItemToBooking(anyLong(), anyString()))
                .thenReturn(items);

        mvc.perform(get(url + "/search")
                        .header(headerUserId, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(content().json(mapper.writeValueAsString(items)))
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void addCommentTest() throws Exception {
        Map<String, Object> newCommentRequest = Map.of(
                "text", "text"
        );
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mvc.perform(post(url + "/" + itemDto.getId() + "/comment")
                        .header(headerUserId, 1L)
                        .content(mapper.writeValueAsString(newCommentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value(commentDto.getText()));
    }
}
