package ru.yandex.practicum.shareIt.user;

import lombok.Data;

@Data
public class UserDto {
    private final int id;
    private final String name;
    private final String email;
}
