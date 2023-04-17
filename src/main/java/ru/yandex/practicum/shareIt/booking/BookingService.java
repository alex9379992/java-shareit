package ru.yandex.practicum.shareIt.booking;


import ru.yandex.practicum.shareIt.booking.model.Booking;
import ru.yandex.practicum.shareIt.booking.model.BookingDto;
import ru.yandex.practicum.shareIt.booking.model.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingResponseDto responseDto, long userId);

    BookingDto patchBooking(long bookingId, boolean approved, long userId);

    BookingDto findBookingDtoById(long bookingId, long userId);

    Booking findBookingById(long booking);

    List<BookingDto> findAllBookingsByBooker(String state, long userId, Long from, Long size);

    List<BookingDto> findAllBookingsByOwner(String state, long userId, Long from, Long size);
}
