package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final EntityManager em;
    private final BookingService bookingService;

    @Test
    void findAllBookingsByUser_IntegrationTest() {
        User owner = new User(null, "owner@mail.com", "Owner");
        em.persist(owner);

        User booker = new User(null, "booker@mail.com", "Booker");
        em.persist(booker);

        Item item = new Item(null, "Drill", "Desc", owner, true, null);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);
        em.persist(booking);

        em.flush();

        Collection<BookingDto> result = bookingService.findAllBookingsByUser(booker.getId(), "ALL");

        assertThat(result, hasSize(1));
        BookingDto dto = result.iterator().next();
        assertThat(dto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(dto.getItem().getName(), equalTo("Drill"));
    }
}
