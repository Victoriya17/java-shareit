package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class BookingMapperTest {

    private User booker;
    private Item item;
    private Booking booking;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);

        booker = new User(1L, "booker@mail.com", "Booker");
        User owner = new User(2L, "owner@mail.com", "Owner");
        item = new Item(10L, "Drill", "Powerful", owner, true, null);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);
    }

    @Test
    void mapToBookingDtoTest() {
        BookingDto result = BookingMapper.mapToBookingDto(booking);

        assertThat(result.getId(), equalTo(booking.getId()));
        assertThat(result.getStart(), equalTo(start));
        assertThat(result.getEnd(), equalTo(end));
        assertThat(result.getStatus(), equalTo(Status.APPROVED));
        assertThat(result.getItem().getId(), equalTo(10L));
        assertThat(result.getBooker().getId(), equalTo(1L));
    }

    @Test
    void mapToBookingTest() {
        NewBookingRequest request = new NewBookingRequest();
        request.setStart(start);
        request.setEnd(end);
        request.setItemId(10L);

        Booking result = BookingMapper.mapToBooking(request, booker, item);

        assertThat(result.getStart(), equalTo(start));
        assertThat(result.getEnd(), equalTo(end));
        assertThat(result.getItem(), equalTo(item));
        assertThat(result.getBooker(), equalTo(booker));
        assertThat(result.getStatus(), equalTo(Status.WAITING));
    }
}