package ru.yandex.practicum.shareIt.item;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.yandex.practicum.shareIt.user.User;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
public class Item {
    private Integer id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NonNull
    private Boolean available;
    private User owner;
    private String request;
}
