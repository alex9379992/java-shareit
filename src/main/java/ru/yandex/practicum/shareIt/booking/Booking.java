package ru.yandex.practicum.shareIt.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Booking {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private int item;
    private int booker;
    private String status;
}
