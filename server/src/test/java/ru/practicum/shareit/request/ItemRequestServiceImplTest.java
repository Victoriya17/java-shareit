package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock private ItemRequestRepository itemRequestRepository;
    @Mock private UserRepository userRepository;
    @Mock private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User(1L, "user@mail.com", "User");
        itemRequest = new ItemRequest(1L, "Need a drill", user, LocalDateTime.now());
    }

    @Test
    void createItemRequestSuccess() {
        NewRequest newRequest = new NewRequest();
        newRequest.setDescription("Need a drill");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.createItemRequest(1L, newRequest);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getDescription(), equalTo("Need a drill"));
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void findAllItemRequestsByRequestorIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        itemRequest.setId(1L);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestIdIn(List.of(1L)))
                .thenReturn(Collections.emptyList());

        Collection<ItemRequestDto> result = itemRequestService.findAllItemRequestsByRequestorId(1L);

        assertThat(result, hasSize(1));

        verify(itemRepository).findAllByRequestIdIn(List.of(1L));
        verify(itemRepository, never()).findByRequestId(anyLong());
    }

    @Test
    void findRequestByIdSuccess() {
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(Collections.emptyList());

        ItemRequestDto result = itemRequestService.findRequestById(1L);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getDescription(), equalTo("Need a drill"));
    }

    @Test
    void findRequestByIdThrowsNotFound() {
        when(itemRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findRequestById(99L));
    }

    @Test
    void findAllItemRequestsSuccess() {
        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(1L))
                .thenReturn(List.of(itemRequest));

        Collection<ItemRequestDto> result = itemRequestService.findAllItemRequests(1L);

        assertThat(result, hasSize(1));
    }
}
