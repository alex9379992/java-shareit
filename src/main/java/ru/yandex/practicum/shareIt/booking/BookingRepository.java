package ru.yandex.practicum.shareIt.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.shareIt.booking.model.Booking;
import ru.yandex.practicum.shareIt.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
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

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start < current_timestamp " +
            "and b.end > current_timestamp " +
            "order by b.start desc ")
    List<Booking> findByBookerIdCurrent(Long userId);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start < current_timestamp " +
            "and b.end > current_timestamp " +
            "order by b.start asc")
    List<Booking> findBookingsByItemOwnerCurrent(Long userId);

    @Query("select b from Booking b " +
            " where b.item.id = ?1 " +
            " and b.booker.id = ?2" +
            " and b.end < current_timestamp")
    List<Booking> findBookingsForAddComments(Long itemId, Long userId);
}
