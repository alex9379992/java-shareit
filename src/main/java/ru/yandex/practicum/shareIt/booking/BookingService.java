package ru.yandex.practicum.shareIt.booking;


import ru.yandex.practicum.shareIt.booking.model.BookingDto;
import ru.yandex.practicum.shareIt.booking.model.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingResponseDto responseDto, long userId);

    BookingDto patchBooking(long bookingId, boolean approved, long userId);

    BookingDto findBookingDtoById(long bookingId, long userId);

    List<BookingDto> findAllBookingsByBooker(String state, long userId);

    List<BookingDto> findAllBookingsByOwner(String state, long userId);
}
