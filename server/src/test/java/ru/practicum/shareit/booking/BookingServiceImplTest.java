package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingStatusException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotItemOwnerException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private NewBookingRequest request;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "booker@mail.com", "Booker");
        owner = new User(2L, "owner@mail.com", "Owner");
        item = new Item(10L, "Drill", "Powerful", owner, true, null);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        request = new NewBookingRequest();
        request.setItemId(10L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void createSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndStatusNotAndStartBeforeAndEndAfter(anyLong(), any(), any(), any()))
                .thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.create(1L, request);

        assertThat(result.getId(), equalTo(1L));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createThrowsExceptionWhenItemNotAvailable() {
        item.setAvailable(false);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));

        assertThrows(ValidationException.class, () -> bookingService.create(1L, request));
    }

    @Test
    void createThrowsExceptionWhenOwnerTriesToBookOwnItem() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));

        assertThrows(NotFoundException.class, () -> bookingService.create(2L, request));
    }

    @Test
    void approveBookingSuccess() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));

        BookingDto result = bookingService.approveBooking(1L, 2L, true);

        assertThat(result.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void approveBookingThrowsExceptionWhenUserNotOwner() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));

        assertThrows(NotItemOwnerException.class,
                () -> bookingService.approveBooking(1L, 1L, true));
    }

    @Test
    void findBookingSuccess() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        BookingDto result = bookingService.findBooking(1L, 1L);

        assertThat(result.getId(), equalTo(1L));
    }

    @Test
    void findAllBookingsByUserAll() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(1L)).thenReturn(List.of(booking));

        Collection<BookingDto> result = bookingService.findAllBookingsByUser(1L, "ALL");

        assertThat(result, hasSize(1));
    }

    @Test
    void createThrowsExceptionWhenOverlapping() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.existsByItemIdAndStatusNotAndStartBeforeAndEndAfter(anyLong(), any(), any(), any()))
                .thenReturn(true);

        assertThrows(ValidationException.class, () -> bookingService.create(1L, request));
    }

    @Test
    void approveBookingThrowsExceptionWhenStatusNotWaiting() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        assertThrows(BookingStatusException.class,
                () -> bookingService.approveBooking(1L, 2L, true));
    }

    @Test
    void findAllBookingsByUserAllStates() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        bookingService.findAllBookingsByUser(1L, "CURRENT");
        bookingService.findAllBookingsByUser(1L, "PAST");
        bookingService.findAllBookingsByUser(1L, "FUTURE");
        bookingService.findAllBookingsByUser(1L, "WAITING");
        bookingService.findAllBookingsByUser(1L, "REJECTED");

        verify(bookingRepository).findAllCurrentBookingsByBookerId(anyLong(), any());
        verify(bookingRepository).findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any());
        verify(bookingRepository).findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any());
        verify(bookingRepository).findByBookerIdAndStatusOrderByStartDesc(anyLong(), eq(Status.WAITING));
        verify(bookingRepository).findByBookerIdAndStatusOrderByStartDesc(anyLong(), eq(Status.REJECTED));
    }

    @Test
    void findAllBookingsByOwnerItemsAllStates() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        bookingService.findAllBookingsByOwnerItems(2L, "ALL");
        bookingService.findAllBookingsByOwnerItems(2L, "CURRENT");
        bookingService.findAllBookingsByOwnerItems(2L, "PAST");
        bookingService.findAllBookingsByOwnerItems(2L, "FUTURE");
        bookingService.findAllBookingsByOwnerItems(2L, "WAITING");
        bookingService.findAllBookingsByOwnerItems(2L, "REJECTED");

        verify(bookingRepository).findAllByItemOwnerId(2L);
        verify(bookingRepository).findAllCurrentBookingsByOwnerId(anyLong(), any());
        verify(bookingRepository).findAllByItemOwnerIdAndEndBefore(anyLong(), any());
        verify(bookingRepository).findAllByItemOwnerIdAndStartAfter(anyLong(), any());
        verify(bookingRepository).findAllByItemOwnerIdAndStatus(anyLong(), eq(Status.WAITING));
        verify(bookingRepository).findAllByItemOwnerIdAndStatus(anyLong(), eq(Status.REJECTED));
    }

    @Test
    void findBookingThrowsExceptionWhenUserNotAuthorized() {
        User stranger = new User(3L, "stranger@mail.com", "Str");
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        assertThrows(ValidationException.class, () -> bookingService.findBooking(1L, 3L));
    }

    @Test
    void findBookingById_ThrowsNotFound() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.findBooking(999L, 1L));
    }

    @Test
    void findUserById_ThrowsNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(999L, request));
    }

    @Test
    void findItemById_ThrowsNotFound() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        request.setItemId(999L);
        assertThrows(NotFoundException.class, () -> bookingService.create(1L, request));
    }
}