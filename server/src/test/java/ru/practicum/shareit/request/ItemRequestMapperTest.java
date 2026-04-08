package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ItemRequestMapperTest {

    private User requestor;
    private ItemRequest itemRequest;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        requestor = new User(1L, "user@mail.com", "User");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need a hammer");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(now);
    }

    @Test
    void mapToItemRequestDtoTest() {
        ItemRequestDto result = ItemRequestMapper.mapToItemRequestDto(itemRequest);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getDescription(), equalTo("Need a hammer"));
        assertThat(result.getRequestorId(), equalTo(1L));
        assertThat(result.getCreated(), equalTo(now));
    }

    @Test
    void mapToItemRequestTest() {
        NewRequest requestDto = new NewRequest();
        requestDto.setDescription("New request");

        ItemRequest result = ItemRequestMapper.mapToItemRequest(requestDto, requestor);

        assertThat(result.getDescription(), equalTo("New request"));
        assertThat(result.getRequestor(), equalTo(requestor));
        assertThat(result.getCreated(), is(notNullValue()));
    }

    @Test
    void mapToItemRequestDtoWithItemsTest() {
        Item item = new Item(10L, "Hammer", "Heavy hammer", requestor, true, itemRequest);

        ItemRequestDto result = ItemRequestMapper.mapToItemRequestDto(itemRequest, List.of(item));

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getItems(), hasSize(1));
        assertThat(result.getItems().get(0).getId(), equalTo(10L));
        assertThat(result.getItems().get(0).getRequestId(), equalTo(1L));
    }

    @Test
    void mapToItemRequestDtoWithNullItemsTest() {
        ItemRequestDto result = ItemRequestMapper.mapToItemRequestDto(itemRequest, null);

        assertThat(result.getItems(), is(notNullValue()));
        assertThat(result.getItems(), is(empty()));
    }

    @Test
    void mapToItemRequestDtoWithEmptyItemsTest() {
        ItemRequestDto result = ItemRequestMapper.mapToItemRequestDto(itemRequest, Collections.emptyList());

        assertThat(result.getItems(), is(empty()));
    }
}