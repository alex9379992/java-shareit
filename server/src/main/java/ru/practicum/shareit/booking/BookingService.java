package ru.practicum.shareit.booking;


import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingResponseDto responseDto, long userId);

    BookingDto patchBooking(long bookingId, boolean approved, long userId);

    BookingDto findBookingDtoById(long bookingId, long userId);

    List<BookingDto> findAllBookingsByBooker(String state, long userId, Long from, Long size);

    List<BookingDto> findAllBookingsByOwner(String state, long userId, Long from, Long size);
}
