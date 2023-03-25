package ru.yandex.practicum.shareIt.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private final int id;
    private final String description;
    private final int requestor;
    private final LocalDateTime created;
}
