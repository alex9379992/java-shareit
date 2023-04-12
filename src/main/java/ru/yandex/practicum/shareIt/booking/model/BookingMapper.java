package ru.yandex.practicum.shareIt.booking.model;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookingMapper {


     Booking toBooking(BookingResponseDto bookingResponseDto);
     BookingDto toBookingDto(Booking booking);

     @Mapping(target = "bookerId", source = "booking.booker.id")
     BookingInItemDto toBookingInItemDto(Booking booking);

     default List<BookingDto> mapToBookingDtoList(List<Booking> bookings) {
          return bookings.stream().map(this::toBookingDto).collect(Collectors.toList());
     }
}
