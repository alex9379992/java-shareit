package ru.yandex.practicum.shareIt.booking.model;


import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static Booking mapToBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(bookingDto.getItem());
        booking.setBooker(bookingDto.getBooker());
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(booking.getItem());
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static BookingInItemDto mapToBookingInItemDto(Booking booking) {
        BookingInItemDto bookingInItemDto = new BookingInItemDto();
        bookingInItemDto.setId(booking.getId());
        bookingInItemDto.setBookerId(booking.getBooker().getId());
        bookingInItemDto.setStart(booking.getStart());
        bookingInItemDto.setEnd(booking.getEnd());
        return bookingInItemDto;
    }

    public static List<BookingDto> mapToBookingDtoList(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::mapToBookingDto).collect(Collectors.toList());
    }
}
