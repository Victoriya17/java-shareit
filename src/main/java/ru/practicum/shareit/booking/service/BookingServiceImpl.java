package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;



    @Override
    @Transactional
    public BookingDto create(Long userId,NewBookingRequest request) {
        log.debug("Запрос на бронирование: userId={}, itemId={}, start={}, end={}",
                userId, request.getItemId(), request.getStart(), request.getEnd());

        Item item = findItemById(request.getItemId());
        User user = findUserById(userId);

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования.");
        }

        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Нельзя бронировать собственную вещь");
        }

        boolean overlapping = bookingRepository.existsByItemIdAndStatusNotAndStartBeforeAndEndAfter(item.getId(),
                Status.REJECTED, request.getEnd(), request.getStart());

        if (overlapping) {
            throw new ValidationException("На эти даты вещь уже занята другим бронированием");
        }

        Booking booking = BookingMapper.mapToBooking(request, user, item);
        booking = bookingRepository.save(booking);

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking = findBookingById(bookingId);
        Item item = findItemById(booking.getItem().getId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Пользователь с ID " + userId + " не найден"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotItemOwnerException("Изменять статус вещи может только её владелец");
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BookingStatusException("Вещь уже забронирована");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findBooking(Long bookingId, Long userId) {
        log.debug("Ищем бронирование с ID {}", bookingId);

        Booking booking = findBookingById(bookingId);
        Item item = findItemById(booking.getItem().getId());
        User owner = findUserById(item.getOwner().getId());
        if (!booking.getBooker().getId().equals(userId) && !owner.getId().equals(userId)) {
            throw new ValidationException("Только владелец вещи и создатель брони могут просматривать данные о " +
                    "бронировании");
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> findAllBookingsByUser(Long userId, String state) {
        State currentState = State.valueOf(state);
        User user = findUserById(userId);
        Collection<Booking> bookingList;
        LocalDateTime now = LocalDateTime.now();

        switch (currentState) {
            case ALL:
                log.debug("Получаем записи о всех бронированиях пользователя");
                bookingList = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                log.debug("Получаем записи о всех текущих бронированиях пользователя");
                bookingList = bookingRepository.findAllCurrentBookingsByBookerId(userId, now);
                break;
            case PAST:
                log.debug("Получаем записи о завершенных бронированиях пользователя");
                bookingList = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                log.debug("Получаем записи о будущих бронированиях пользователя");
                bookingList = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                log.debug("Получаем записи бронирований ожидающих подтверждения пользователя");
                bookingList = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                log.debug("Получаем записи об отклоненных бронированиях пользователя");
                bookingList = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookingList.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> findAllBookingsByOwnerItems(Long userId, String state) {
        State currentState = State.valueOf(state);
        User findUser = findUserById(userId);
        Collection<Booking> bookingList;
        LocalDateTime now = LocalDateTime.now();

        switch (currentState) {
            case ALL:
                log.debug("Получаем записи бронирований вещей пользователя");
                bookingList = bookingRepository.findAllByItemOwnerId(userId);
                break;
            case CURRENT:
                log.debug("Получаем записи о всех текущих бронированиях вещей пользователя");
                bookingList = bookingRepository.findAllCurrentBookingsByOwnerId(userId, now);
                break;
            case PAST:
                log.debug("Получаем записи о завершенных бронированиях вещей пользователя");
                bookingList = bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, now);
                break;
            case FUTURE:
                log.debug("Получаем записи о будущих бронированиях вещей пользователя");
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, now);
                break;
            case WAITING:
                log.debug("Получаем записи о бронировании вещей пользователя ожидающих подтверждения");
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.WAITING);
                break;
            case REJECTED:
                log.debug("Получаем записи об отклоненных бронированиях вещей пользователя");
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookingList.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование c ID " + bookingId + "не найдено"));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c ID " + userId + ", создающий бронирование, " +
                        "не найден"));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь для бронирования c ID " + itemId + "не найдена"));
    }
}
