package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItemId(booking.getItemId());
        dto.setBookerId(booking.getBookerId());
        dto.setStatus(booking.getStatus());
        return dto;
    }

    public static Booking mapToBooking(NewBookingRequest request) {
        Booking booking = new Booking();
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setItemId(request.getItemId());
        booking.setBookerId(request.getBookerId());
        booking.setStatus(request.getStatus());
        return booking;
    }

    public static Booking updateBookingFields(Booking booking, UpdateBookingRequest request) {
        if (request.hasStart()) {
            booking.setStart(request.getStart());
        }
        if (request.hasEnd()) {
            booking.setEnd(request.getEnd());
        }
        return booking;
    }
}
