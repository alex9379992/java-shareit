package ru.yandex.practicum.shareIt.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.shareIt.booking.model.Booking;
import ru.yandex.practicum.shareIt.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndEndIsBefore(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndStartIsAfter(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findBookingByItemOwnerId(Long bookerId, Sort sort);

    List<Booking> findBookingByItemIdAndStartAfterAndStatus(Long itemId, LocalDateTime now, Sort sort, BookingStatus status);

    List<Booking> findBookingByItemIdAndStartBeforeAndStatus(Long itemId, LocalDateTime now, Sort sort, BookingStatus status);

    @Query(value = "select * from bookings  " +
            "where booker_id = ?1 " +
            "and bookings.start_date < current_timestamp " +
            "and bookings.end_date > current_timestamp " +
            "order by start_date ", nativeQuery = true)
    List<Booking> findByBookerIdCurrent(Long userId);

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings b " +
            "inner join items i on i.id = b.item_id " +
            "where i.owner_id = ?1 " +
            "and b.start_date < current_timestamp " +
            "and b.end_date > current_timestamp " +
            "order by b.start_date ", nativeQuery = true)
    List<Booking> findBookingsByItemOwnerCurrent(Long userId);

    @Query(value = "select b.id, b.start_date, b.end_date, b.item_id, b.booker_id, b.status " +
            "from bookings b " +
            "inner join items i on i.id = b.item_id " +
            " where i.id = ?1 " +
            " and b.booker_id = ?2" +
            " and b.end_date < current_timestamp", nativeQuery = true)
    List<Booking> findBookingsForAddComments(Long itemId, Long userId);
}
