package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotItemOwnerException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock private ItemRepository itemRepository;
    @Mock private UserRepository userRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1L, "user@mail.com", "User");
        item = new Item(10L, "Drill", "Powerful", user, true, null);
    }

    @Test
    void createItemSuccess() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Drill");
        request.setDescription("Powerful");
        request.setAvailable(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.createItem(1L, request);

        assertThat(result.getName(), equalTo("Drill"));
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItemByOwnerSuccess() {
        UpdateItemRequest update = new UpdateItemRequest();
        update.setName("New Name");

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.updateItem(10L, update, 1L);

        assertThat(result.getName(), equalTo("New Name"));
    }

    @Test
    void updateItemByNotOwnerThrowsException() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "u@m.c", "U")));

        assertThrows(NotItemOwnerException.class,
                () -> itemService.updateItem(10L, new UpdateItemRequest(), 2L));
    }

    @Test
    void findItemByIdByOwnerShouldIncludeBookings() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(10L)).thenReturn(Collections.emptyList());
        when(bookingRepository.findLastBookingEndByItemId(anyLong(), any(), any()))
                .thenReturn(List.of(LocalDateTime.now().minusDays(1)));

        OtherItemDto result = itemService.findItemById(1L, 10L);

        assertThat(result.getLastBooking(), notNullValue());
    }

    @Test
    void addCommentSuccess() {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("Good");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment(1L, "Good", item, user, LocalDateTime.now()));

        CommentDto result = itemService.addComment(10L, 1L, request);

        assertThat(result.getText(), equalTo("Good"));
    }

    @Test
    void addCommentWithoutBookingThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(false);

        assertThrows(ValidationException.class,
                () -> itemService.addComment(10L, 1L, new NewCommentRequest()));
    }
}