package ru.yandex.practicum.shareIt.booking;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.shareIt.BaseTest;
import ru.yandex.practicum.shareIt.booking.model.Booking;
import ru.yandex.practicum.shareIt.booking.model.BookingStatus;
import ru.yandex.practicum.shareIt.item.ItemRepository;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.request.RequestRepository;
import ru.yandex.practicum.shareIt.request.model.Request;
import ru.yandex.practicum.shareIt.user.UserRepository;
import ru.yandex.practicum.shareIt.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@TestPropertySource(properties = { "db.name=test"})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookingRepositoryTest extends BaseTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RequestRepository requestRepository;

    private final Sort sort = Sort.by("start").descending();

    @BeforeEach
    void setUp() {
        User owner = createUser(1L, "alex", "email@gmail.com");
        owner = userRepository.save(owner);

        User booker = createUser(2L, "leo", "leo@mail.com");
        booker = userRepository.save(booker);

        Request request = createRequest(1L, booker, "Нужна отвертка");
        request = requestRepository.save(request);

        Item item = crateItem();
        item.setId(1L);
        item.setOwner(owner);
        item.setRequest(request);
        item = itemRepository.save(item);

        Booking bookingWaiting = createBooking(item, booker);
        bookingWaiting.setId(1L);
        bookingWaiting.setStart(LocalDateTime.now().minusDays(1));
        bookingWaiting.setEnd(LocalDateTime.now().plusDays(1));
        bookingWaiting.setStatus(BookingStatus.WAITING);
        bookingWaiting = bookingRepository.save(bookingWaiting);

        Booking bookingApproved = createBooking(item, booker);
        bookingApproved.setId(2L);
        bookingApproved.setEnd(bookingWaiting.getEnd().plusDays(2));
        bookingApproved.setStart(bookingWaiting.getStart().plusDays(1));
        bookingApproved = bookingRepository.save(bookingApproved);

        Booking bookingApproved2 = createBooking(item, booker);
        bookingApproved2.setId(3L);
        bookingApproved2.setEnd(bookingApproved.getEnd().plusDays(2));
        bookingApproved2.setStart(bookingApproved.getStart().plusDays(1));
        bookingApproved2 = bookingRepository.save(bookingApproved2);

        Booking bookingApproved3 = createBooking(item, booker);
        bookingApproved3.setId(4L);
        bookingApproved3.setEnd(bookingApproved2.getEnd().plusDays(2));
        bookingApproved3.setStart(bookingApproved2.getStart().plusDays(1));
        bookingApproved3 = bookingRepository.save(bookingApproved3);

        Booking bookingRejected = createBooking(item, booker);
        bookingRejected.setId(5L);
        bookingRejected.setEnd(bookingApproved3.getEnd().plusDays(2));
        bookingRejected.setStart(bookingApproved3.getStart().plusDays(1));
        bookingRejected.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(bookingRejected);
    }


    @Test
    void findByBookerIdAndStatusTest() {
        List<Booking> actual = bookingRepository.findByBookerIdAndStatus(2L, BookingStatus.APPROVED, sort);
        assertEquals(actual.size(), 3);
        for (Booking booking : actual) {
            assertEquals(booking.getStatus(), BookingStatus.APPROVED);
        }
    }

   @Test
    void findByBookerIdAndEndIsBefore() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBefore(2L, LocalDateTime.now(), sort);
       for (Booking booking : bookings) {
           assertTrue(booking.getEnd().isBefore(LocalDateTime.now()));
       }
   }

   @Test
    void findByBookerIdAndStartIsAfterTest() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartIsAfter(2L, LocalDateTime.now(), sort);
        assertEquals(bookings.size(), 3);
       for (Booking booking : bookings) {
           assertTrue(booking.getStart().isAfter(LocalDateTime.now()));
       }
   }

   @Test
    void findByBookerIdTest() {
       List<Booking> bookings = bookingRepository.findByBookerId(2L, sort);
       assertEquals(bookings.size(), 5);
       for (Booking booking : bookings) {
          assertEquals(booking.getBooker().getId(), 2L);
       }
   }

   @Test
    void findBookingByItemOwnerIdAndStatusFromBookingStatusWAITINGTest() {
        List<Booking> bookings = bookingRepository.findBookingByItemOwnerIdAndStatus(1L, BookingStatus.WAITING, sort);
        assertEquals(bookings.size(), 1);
       for (Booking booking : bookings) {
           assertEquals(booking.getItem().getOwner().getId(), 1L);
           assertEquals(booking.getStatus(), BookingStatus.WAITING);
       }
   }

    @Test
    void findBookingByItemOwnerIdAndStatusFromBookingStatusRejectedTest() {
        List<Booking> bookings = bookingRepository.findBookingByItemOwnerIdAndStatus(1L, BookingStatus.REJECTED, sort);
        assertEquals(bookings.size(), 1);
        for (Booking booking : bookings) {
            assertEquals(booking.getItem().getOwner().getId(), 1L);
            assertEquals(booking.getStatus(), BookingStatus.REJECTED);
        }
    }

    @Test
    void findBookingByItemOwnerIdAndEndIsBeforeTest() {
        List<Booking> bookings = bookingRepository.findBookingByItemOwnerIdAndEndIsBefore(1L, LocalDateTime.now(), sort);
        for (Booking booking : bookings) {
            assertEquals(booking.getItem().getOwner().getId(), 1L);
            assertTrue(booking.getEnd().isBefore(LocalDateTime.now()));
        }
    }

    @Test
    void findBookingByItemOwnerIdAndStartIsAfterTest() {
        List<Booking> bookings = bookingRepository.findBookingByItemOwnerIdAndStartIsAfter(1L, LocalDateTime.now(), sort);
        for (Booking booking : bookings) {
            assertEquals(booking.getItem().getOwner().getId(), 1L);
            assertTrue(booking.getStart().isAfter(LocalDateTime.now()));
        }
    }
    @Test
    void findBookingByItemOwnerIdTest() {
        List<Booking> bookings = bookingRepository.findBookingByItemOwnerId(1L, sort);
        assertEquals(bookings.size(), 5);
        for (Booking booking : bookings) {
            assertEquals(booking.getItem().getOwner().getId(), 1L);
        }
    }

    @Test
    void findBookingByItemIdAndStartAfterAndStatusTest() {
        List<Booking> bookings = bookingRepository.findBookingByItemIdAndStartAfterAndStatus(1L, LocalDateTime.now(), sort, BookingStatus.APPROVED);
        for (Booking booking : bookings) {
            assertEquals(booking.getItem().getId(), 1L);
            assertTrue(booking.getStart().isAfter(LocalDateTime.now()));
        }
    }

    @Test
    void findBookingByItemIdAndStartBeforeAndStatusTest() {
        List<Booking> bookings = bookingRepository.findBookingByItemIdAndStartBeforeAndStatus(1L, LocalDateTime.now(), sort, BookingStatus.APPROVED);
        for (Booking booking : bookings) {
            assertEquals(booking.getItem().getId(), 1L);
            assertTrue(booking.getStart().isBefore(LocalDateTime.now()));
        }
    }

    @Test
    void findByBookerIdCurrentTest() {
    List<Booking> bookings = bookingRepository.findByBookerIdCurrent(2L);
        for (Booking booking : bookings) {
            assertEquals(booking.getItem().getId(), 1L);
            assertTrue(booking.getStart().isBefore(LocalDateTime.now()));
            assertTrue(booking.getEnd().isAfter(LocalDateTime.now()));
        }
    }

    @Test
    void findBookingsByItemOwnerCurrentTest() {
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerCurrent(1L);
        for (Booking booking : bookings) {
            assertEquals(booking.getItem().getOwner().getId(), 1L);
            assertTrue(booking.getStart().isBefore(LocalDateTime.now()));
            assertTrue(booking.getEnd().isAfter(LocalDateTime.now()));
        }
    }

    @Test
    void findBookingsForAddComments() {
        List<Booking> bookings = bookingRepository.findBookingsForAddComments(1L, 2L);
        for (Booking booking : bookings) {
            assertEquals(booking.getItem().getId(), 1L);
            assertEquals(booking.getBooker().getId(), 2L);
            assertTrue(booking.getEnd().isBefore(LocalDateTime.now()));
        }
    }
}