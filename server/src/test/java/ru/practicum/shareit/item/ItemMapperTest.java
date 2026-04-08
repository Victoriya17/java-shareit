package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.OtherItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ItemMapperTest {

    private User owner;
    private ItemRequest itemRequest;
    private Item item;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        owner = new User(1L, "owner@mail.com", "Owner");
        itemRequest = new ItemRequest(5L, "need drill", owner, now);
        item = new Item(10L, "Drill", "Powerful", owner, true, itemRequest);
    }

    @Test
    void mapToItemDtoTest() {
        ItemDto result = ItemMapper.mapToItemDto(item);

        assertThat(result.getId(), equalTo(10L));
        assertThat(result.getName(), equalTo("Drill"));
        assertThat(result.getAvailable(), is(true));
        assertThat(result.getOwnerId(), equalTo(1L));
        assertThat(result.getRequestId(), equalTo(5L));
    }

    @Test
    void mapToItemDtoWithoutRequestTest() {
        item.setRequest(null);

        ItemDto result = ItemMapper.mapToItemDto(item);

        assertThat(result.getRequestId(), is(nullValue()));
    }

    @Test
    void mapToItemTest() {
        NewItemRequest dto = new NewItemRequest();
        dto.setName("Hammer");
        dto.setDescription("Heavy");
        dto.setAvailable(true);

        Item result = ItemMapper.mapToItem(owner, dto, itemRequest);

        assertThat(result.getName(), equalTo("Hammer"));
        assertThat(result.getOwner(), equalTo(owner));
        assertThat(result.getRequest(), equalTo(itemRequest));
    }

    @Test
    void updateItemFieldsTest() {
        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setName("Updated Name");

        Item result = ItemMapper.updateItemFields(item, updateRequest);

        assertThat(result.getName(), equalTo("Updated Name"));
        assertThat(result.getDescription(), equalTo("Powerful"));
        assertThat(result.getAvailable(), is(true));
    }

    @Test
    void mapToOtherItemDtoFullTest() {
        Comment comment = createComment();
        LocalDateTime last = now.minusDays(1);
        LocalDateTime next = now.plusDays(1);

        OtherItemDto result = ItemMapper.mapToOtherItemDto(
                item,
                List.of(comment),
                Optional.of(last),
                Optional.of(next)
        );

        assertThat(result.getId(), equalTo(10L));
        assertThat(result.getLastBooking(), equalTo(last));
        assertThat(result.getNextBooking(), equalTo(next));
        assertThat(result.getComments(), hasSize(1));
    }

    @Test
    void mapToOtherItemDtoShortTest() {
        Comment comment = createComment();

        OtherItemDto result = ItemMapper.mapToOtherItemDto(item, List.of(comment));

        assertThat(result.getId(), equalTo(10L));
        assertThat(result.getComments(), hasSize(1));
        assertThat(result.getLastBooking(), is(nullValue()));
    }

    @Test
    void mapToOtherItemDtoEmptyOptionalsTest() {
        OtherItemDto result = ItemMapper.mapToOtherItemDto(
                item,
                List.of(),
                Optional.empty(),
                Optional.empty()
        );

        assertThat(result.getLastBooking(), is(nullValue()));
        assertThat(result.getNextBooking(), is(nullValue()));
    }

    @Test
    void mapToOtherItemDtoNoRequestTest() {
        item.setRequest(null);

        OtherItemDto result = ItemMapper.mapToOtherItemDto(item, List.of());

        assertThat(result.getRequestId(), is(nullValue()));
    }

    @Test
    void updateItemFieldsFullTest() {
        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setName("New Name");
        updateRequest.setDescription("New Desc");
        updateRequest.setAvailable(false);

        Item result = ItemMapper.updateItemFields(item, updateRequest);

        assertThat(result.getName(), equalTo("New Name"));
        assertThat(result.getDescription(), equalTo("New Desc"));
        assertThat(result.getAvailable(), is(false));
    }

    @Test
    void mapToOtherItemDtoWithRequestFullTest() {
        OtherItemDto result = ItemMapper.mapToOtherItemDto(item, List.of());
        assertThat(result.getRequestId(), equalTo(itemRequest.getId()));
    }

    @Test
    void testPrivateConstructor() throws Exception {
        var constructor = ItemMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ItemMapper instance = constructor.newInstance();
        assertThat(instance, notNullValue());
    }

    private Comment createComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Nice!");
        comment.setItem(item);
        comment.setAuthor(owner);
        comment.setCreated(now);
        return comment;
    }
}