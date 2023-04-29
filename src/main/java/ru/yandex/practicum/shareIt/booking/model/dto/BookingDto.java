package ru.yandex.practicum.shareIt.booking.model.dto;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.shareIt.booking.model.BookingStatus;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.user.model.User;


import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {
    private Long id;
    @NotNull
    @FutureOrPresent
    private  LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
    private Long itemId;
    private Item item;
    private User booker;
    private BookingStatus status;
}
