package ru.yandex.practicum.shareIt.request.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Getter
@Setter
public class IncomingRequest {
    @NotEmpty
    @NotNull
    private String description;
}
