package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody NewBookingRequest booking) {
        return bookingClient.create(userId, booking);
    }

    @PatchMapping("/{booking-id}")
    public ResponseEntity<Object> approveBooking(@PathVariable("booking-id") Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(name = "approved", defaultValue = "false")
                                                 Boolean approved) {
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{booking-id}")
    public ResponseEntity<Object> findBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable("booking-id") Long bookingId) {
        return bookingClient.findBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestParam(name = "state", defaultValue = "ALL") String state,
                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingClient.findAllBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingsByOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestParam(name = "state", defaultValue = "ALL") String state,
                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingClient.findAllBookingsByOwnerItems(userId, state, from, size);
    }
}
