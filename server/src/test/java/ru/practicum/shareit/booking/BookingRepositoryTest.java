package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TestEntityManager entityManager;

    private User owner;
    private User booker;
    private Item item;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        owner = entityManager.persist(new User(null, "Owner", "owner@mail.com"));
        booker = entityManager.persist(new User(null, "Booker", "booker@mail.com"));
        item = entityManager.persist(new Item(null, "Drill", "Desc", owner, true, null));

        entityManager.flush();
    }

    @Test
    void findAllCurrentBookingsByBookerIdTest() {
        Booking booking = createBooking(now.minusDays(1), now.plusDays(1), booker, Status.APPROVED);

        Collection<Booking> result = bookingRepository.findAllCurrentBookingsByBookerId(booker.getId(), now);

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getId(), equalTo(booking.getId()));
    }

    @Test
    void findAllByItemOwnerIdAndStatusTest() {
        createBooking(now.plusDays(1), now.plusDays(2), booker, Status.WAITING);

        Collection<Booking> result = bookingRepository.findAllByItemOwnerIdAndStatus(owner.getId(), Status.WAITING);

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void findByItemIdInAndStatusAndStartBeforeTest() {
        Booking pastBooking = createBooking(now.minusDays(5), now.minusDays(2), booker, Status.APPROVED);

        List<Booking> result = bookingRepository.findByItemIdInAndStatusAndStartBefore(
                List.of(item.getId()), Status.APPROVED, now);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(pastBooking.getId()));
    }

    @Test
    void findLastAndNextBookingsTest() {
        createBooking(now.minusDays(10), now.minusDays(5), booker, Status.APPROVED);
        createBooking(now.plusDays(5), now.plusDays(10), booker, Status.APPROVED);

        List<LocalDateTime> last = bookingRepository.findLastBookingEndByItemId(item.getId(), Status.APPROVED, now);
        List<LocalDateTime> next = bookingRepository.findNextBookingStartByItemId(item.getId(), Status.APPROVED, now);

        assertThat(last, not(empty()));
        assertThat(next, not(empty()));
        assertTrue(last.get(0).isBefore(now), "Дата окончания должна быть в прошлом");
        assertTrue(next.get(0).isAfter(now), "Дата начала должна быть в будущем");
    }

    @Test
    void existsByItemIdAndOverlappingDatesTest() {
        createBooking(now.plusDays(2), now.plusDays(5), booker, Status.APPROVED);

        boolean exists = bookingRepository.existsByItemIdAndStatusNotAndStartBeforeAndEndAfter(
                item.getId(), Status.REJECTED, now.plusDays(3), now.plusDays(1));

        assertThat(exists, is(true));
    }

    private Booking createBooking(LocalDateTime start, LocalDateTime end, User booker, Status status) {
        Booking b = new Booking();
        b.setStart(start);
        b.setEnd(end);
        b.setBooker(booker);
        b.setItem(item);
        b.setStatus(status);
        return bookingRepository.save(b);
    }
}
