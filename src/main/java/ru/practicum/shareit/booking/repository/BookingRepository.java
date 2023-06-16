package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long bookerId, LocalDateTime date1,
                                                                              LocalDateTime date2);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByEndDesc(long bookerId, LocalDateTime date);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime date);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status);

    List<Booking> findAllByItemIdInOrderByStartDesc(Set<Long> items);

    List<Booking> findByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Set<Long> items,LocalDateTime date1,
                                                                              LocalDateTime date2);

    List<Booking> findByItemIdInAndEndIsBefore(Set<Long> items,LocalDateTime date, Sort sort);

    List<Booking> findByItemIdInAndEndIsAfterOrderByStartDesc(Set<Long> items,LocalDateTime date);

    List<Booking> findByItemIdInAndStatus(Set<Long> items, Status status);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndIsBefore(long userId, long itemId, Status status,
                                                                 LocalDateTime date);

    @Query(value = "SELECT * FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.item_id = :itemId " +
            "AND b.start_date < :date " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date DESC LIMIT 1", nativeQuery = true)
    Optional<Booking> getLastBooking(@Param("itemId") Long id, @Param("date") LocalDateTime date);

    @Query(value = "SELECT * FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.item_id = :itemId " +
            "AND b.start_date > :date " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date ASC LIMIT 1 ", nativeQuery = true)
    Optional<Booking> getNextBooking(@Param("itemId") Long id, @Param("date") LocalDateTime date);
}