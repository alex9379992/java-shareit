package ru.yandex.practicum.shareIt.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class User {
    private int id;
    @NotNull
    private String name;
    @Email
    @NotNull
    private String email;
}
