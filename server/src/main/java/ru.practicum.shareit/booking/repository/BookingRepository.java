package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    Collection<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    Collection<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    Collection<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 AND ?2 BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllCurrentBookingsByBookerId(Long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllByItemOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, Status status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 AND ?2 BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllCurrentBookingsByOwnerId(Long ownerId, LocalDateTime now);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :ids AND b.status = :status AND b.start < :now " +
            "ORDER BY b.end DESC")
    List<Booking> findByItemIdInAndStatusAndStartBefore(List<Long> ids, Status status, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :ids AND b.status = :status AND b.start > :now " +
            "ORDER BY b.start ASC")
    List<Booking> findByItemIdInAndStatusAndStartAfter(List<Long> ids, Status status, LocalDateTime now);

    @Query("SELECT b.end FROM Booking b " +
            "WHERE b.item.id = ?1 AND b.status = ?2 AND b.end < ?3 " +
            "ORDER BY b.end DESC")
    List<LocalDateTime> findLastBookingEndByItemId(Long itemId, Status status, LocalDateTime now);

    @Query("SELECT b.start FROM Booking b " +
            "WHERE b.item.id = ?1 AND b.status = ?2 AND b.start > ?3 " +
            "ORDER BY b.start ASC")
    List<LocalDateTime> findNextBookingStartByItemId(Long itemId, Status status, LocalDateTime now);

    boolean existsByItemIdAndStatusNotAndStartBeforeAndEndAfter(Long itemId, Status status, LocalDateTime end,
                                                                LocalDateTime start);
}
