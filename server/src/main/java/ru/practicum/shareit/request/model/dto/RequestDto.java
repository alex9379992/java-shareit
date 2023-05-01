package ru.practicum.shareit.request.model.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.dto.ItemResponseDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class RequestDto {

    private Long id;

    private String description;

    private User requestor;

    private LocalDateTime created;

    private List<ItemResponseDto> items;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestDto that = (RequestDto) o;
        return Objects.equals(id, that.id) && Objects.equals(description, that.description) && Objects.equals(requestor, that.requestor) && Objects.equals(created, that.created) && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, requestor, created, items);
    }
}
