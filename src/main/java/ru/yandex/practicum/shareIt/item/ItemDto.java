package ru.yandex.practicum.shareIt.item;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.shareIt.user.User;

@Getter
@Setter
@Builder
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private String request;
}
