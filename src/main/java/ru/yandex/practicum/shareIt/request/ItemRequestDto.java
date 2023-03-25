package ru.yandex.practicum.shareIt.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ItemRequestDto {
    private int id;
    private  String description;
    private  int requestor;
    private LocalDateTime created;
}
