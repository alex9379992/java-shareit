package ru.yandex.practicum.shareIt.comment.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CommentRequestDto {
    @NotNull
    @NotEmpty
    private String text;
}
