package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long bookerId, LocalDateTime currentDate1,
                                                                              LocalDateTime currentDate2);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByEndDesc(long bookerId, LocalDateTime currentDate);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime currentDate);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status);

    List<Booking> findAllByItemIdInOrderByStartDesc(Set<Long> items);

    List<Booking> findByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Set<Long> items,LocalDateTime currentDate1,
                                                                              LocalDateTime currentDate2);

    List<Booking> findByItemIdInAndEndIsBefore(Set<Long> items,LocalDateTime currentDate, Sort sort);

    List<Booking> findByItemIdInAndEndIsAfterOrderByStartDesc(Set<Long> items,LocalDateTime currentDate);

    List<Booking> findByItemIdInAndStatus(Set<Long> items, Status status);

    @Query(value = "SELECT * FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.item_id = ?1 " +
            "AND b.start_date < ?2 " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date DESC LIMIT 1", nativeQuery = true)
    Optional<Booking> getLastBooking(Long id, LocalDateTime currentTime);

    @Query(value = "SELECT * FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.item_id = ?1 " +
            "AND b.start_date > ?2 " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date ASC LIMIT 1 ", nativeQuery = true)
    Optional<Booking> getNextBooking(Long id, LocalDateTime currentTime);
}