package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.BookingStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final EntityManager em;
    private final BookingService bookingService;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(null, "owner@mail.com", "Owner");
        em.persist(owner);
        booker = new User(null, "booker@mail.com", "Booker");
        em.persist(booker);
        item = new Item(null, "Дрель", "Описание", owner, true, null);
        em.persist(item);
        em.flush();
    }

    @Test
    void createShouldThrowExceptionWhenOverlapping() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        em.persist(new Booking(null, start, end, item, booker, Status.APPROVED));
        em.flush();

        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(start);
        request.setEnd(end);

        assertThrows(ValidationException.class, () -> bookingService.create(booker.getId(), request));
    }

    @Test
    void createShouldThrowExceptionWhenItemNotAvailable() {
        item.setAvailable(false);
        em.merge(item);
        em.flush();

        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(ValidationException.class, () -> bookingService.create(booker.getId(), request));
    }

    @Test
    void approveBookingShouldThrowIfAlreadyApproved() {
        Booking booking = new Booking(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, Status.APPROVED);
        em.persist(booking);
        em.flush();

        assertThrows(BookingStatusException.class, () ->
                bookingService.approveBooking(booking.getId(), owner.getId(), true));
    }

    @Test
    void findAllBookingsByOwnerItemsShouldCheckAllStates() {
        LocalDateTime now = LocalDateTime.now();
        em.persist(new Booking(null, now.minusDays(5), now.minusDays(4), item, booker, Status.APPROVED));
        em.persist(new Booking(null, now.minusDays(1), now.plusDays(1), item, booker, Status.APPROVED));
        em.persist(new Booking(null, now.plusDays(2), now.plusDays(3), item, booker, Status.WAITING));
        em.persist(new Booking(null, now.plusDays(4), now.plusDays(5), item, booker, Status.REJECTED));
        em.flush();

        assertThat(bookingService.findAllBookingsByOwnerItems(owner.getId(), "ALL"), hasSize(4));
        assertThat(bookingService.findAllBookingsByOwnerItems(owner.getId(), "PAST"), hasSize(1));
        assertThat(bookingService.findAllBookingsByOwnerItems(owner.getId(), "CURRENT"), hasSize(1));
        assertThat(bookingService.findAllBookingsByOwnerItems(owner.getId(), "FUTURE"), hasSize(2));
        assertThat(bookingService.findAllBookingsByOwnerItems(owner.getId(), "WAITING"), hasSize(1));
        assertThat(bookingService.findAllBookingsByOwnerItems(owner.getId(), "REJECTED"), hasSize(1));
    }

    @Test
    void findAllBookingsShouldThrowOnUnknownState() {
        assertThrows(IllegalArgumentException.class, () ->
                bookingService.findAllBookingsByUser(booker.getId(), "UNSUPPORTED"));
    }

    @Test
    void findBookingShouldThrowWhenUserNotOwnerOrBooker() {
        User stranger = new User(null, "stranger@mail.com", "Str");
        em.persist(stranger);
        Booking booking = new Booking(null, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, Status.WAITING);
        em.persist(booking);
        em.flush();

        assertThrows(ValidationException.class, () -> bookingService.findBooking(booking.getId(), stranger.getId()));
    }
}