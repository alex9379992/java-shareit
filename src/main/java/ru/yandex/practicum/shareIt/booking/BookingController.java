package ru.yandex.practicum.shareIt.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.shareIt.booking.model.BookingDto;
import ru.yandex.practicum.shareIt.booking.model.BookingResponseDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @Valid @RequestBody BookingResponseDto responseDto) {
        return ResponseEntity.ok().body(bookingService.createBooking(responseDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> patchBooking(@PathVariable long bookingId, @RequestParam boolean approved,
                                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok().body(bookingService.patchBooking(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> findBookingById(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok().body(bookingService.findBookingDtoById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> findAllBookingsByBooker(@RequestParam(defaultValue = "ALL") String state,
                                                                    @RequestHeader("X-Sharer-User-Id") long userId,
                                                                    @RequestParam(required = false) Long from,
                                                                    @RequestParam(required = false) Long size) {
        return ResponseEntity.ok().body(bookingService.findAllBookingsByBooker(state, userId, from, size));

    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> findAllBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                                   @RequestHeader("X-Sharer-User-Id") long userId,
                                                                   @RequestParam(required = false) Long from,
                                                                   @RequestParam(required = false) Long size) {
        return ResponseEntity.ok().body(bookingService.findAllBookingsByOwner(state, userId, from, size));

    }
}
