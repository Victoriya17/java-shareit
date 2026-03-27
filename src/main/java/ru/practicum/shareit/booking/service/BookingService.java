package ru.practicum.shareit.booking.service;

import jakarta.validation.Valid;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.Collection;

public interface BookingService {
    BookingDto create(Long userId, @Valid NewBookingRequest request);

    BookingDto approveBooking(Long bookingId, Long userId, Boolean approved);

    BookingDto findBooking(Long bookingId, Long userId);

    Collection<BookingDto> findAllBookingsByUser(Long userId, String state);

    Collection<BookingDto> findAllBookingsByOwnerItems(Long userId, String state);
}
