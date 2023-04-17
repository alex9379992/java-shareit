package ru.yandex.practicum.shareIt.request.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.shareIt.response.model.ResponseDto;
import ru.yandex.practicum.shareIt.user.model.User;


import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class RequestDto {

    private Long id;

    private String description;

    private User requestor;

    private LocalDateTime created;

    private List<ResponseDto> items;
}
