package ru.yandex.practicum.shareIt.item.model.dto;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.shareIt.booking.model.dto.BookingInItemDto;
import ru.yandex.practicum.shareIt.comment.model.CommentDto;
import ru.yandex.practicum.shareIt.user.model.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ItemDto {
    private Long id;



    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private BookingInItemDto lastBooking;
    private BookingInItemDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return Objects.equals(id, itemDto.id) && Objects.equals(name, itemDto.name) && Objects.equals(description, itemDto.description) && Objects.equals(available, itemDto.available) && Objects.equals(owner, itemDto.owner) && Objects.equals(lastBooking, itemDto.lastBooking) && Objects.equals(nextBooking, itemDto.nextBooking) && Objects.equals(comments, itemDto.comments) && Objects.equals(requestId, itemDto.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, owner, lastBooking, nextBooking, comments, requestId);
    }
}
