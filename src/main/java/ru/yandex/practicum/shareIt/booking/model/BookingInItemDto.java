package ru.yandex.practicum.shareIt.booking.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter

public class BookingInItemDto {
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingInItemDto that = (BookingInItemDto) o;
        return Objects.equals(id, that.id) && Objects.equals(bookerId, that.bookerId) && Objects.equals(start, that.start) && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookerId, start, end);
    }
}
