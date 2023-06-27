package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        User user = new User("user", "mail@gmail.com");

        Item item = new Item(user, "Правильная вещь", "description1");

        Item wrongItem = new Item(user, "Неправильная вещь.", "description2");

        Booking tooOldBooking = new Booking(LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(5).plusMinutes(60),
                item,
                user,
                Status.APPROVED);

        //id2
        Booking lastBooking = new Booking(LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(4).plusMinutes(60),
                item,
                user,
                Status.APPROVED); //id2

        Booking wrongItemLastBooking = new Booking(LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(3).plusMinutes(60),
                wrongItem,
                user,
                Status.APPROVED);

        Booking wrongStatusLastBooking = new Booking(LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(2).plusMinutes(60),
                item,
                user,
                Status.REJECTED);

        Booking wrongStatusFutureBooking = new Booking(LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusMinutes(60),
                item,
                user,
                Status.REJECTED);

        Booking wrongItemFutureBooking = new Booking(LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(3).plusMinutes(60),
                wrongItem,
                user,
                Status.APPROVED);

        //id7
        Booking futureBooking = new Booking(LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(4).plusMinutes(60),
                item,
                user,
                Status.APPROVED); //id7

        Booking tooFarBooking = new Booking(LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(5).plusMinutes(60),
                item,
                user,
                Status.APPROVED);

        em.persist(user);
        em.persist(item);
        em.persist(wrongItem);
        em.flush();
        bookingRepository.save(tooOldBooking);
        bookingRepository.save(lastBooking);
        bookingRepository.save(wrongItemLastBooking);
        bookingRepository.save(wrongStatusLastBooking);
        bookingRepository.save(wrongStatusFutureBooking);
        bookingRepository.save(wrongItemFutureBooking);
        bookingRepository.save(futureBooking);
        bookingRepository.save(tooFarBooking);
    }

    @Test
    void getLastBooking() {
        Optional<Booking> lastBookingResult = bookingRepository.getLastBooking(1L, LocalDateTime.now());
        assert (lastBookingResult.isPresent());

        Booking bookingResult = lastBookingResult.get();
        assertEquals(1, bookingResult.getItem().getId());
        assertEquals(2, bookingResult.getId());
    }

    @Test
    void getNextBooking() {
        Optional<Booking> futureBookingResult = bookingRepository.getNextBooking(1L, LocalDateTime.now());
        assert (futureBookingResult.isPresent());

        Booking bookingResult = futureBookingResult.get();
        assertEquals(1, bookingResult.getItem().getId());
        assertEquals(7, bookingResult.getId());
    }
}