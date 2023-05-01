package ru.yandex.practicum.shareIt.item;

import ru.yandex.practicum.shareIt.comment.model.CommentRequestDto;
import ru.yandex.practicum.shareIt.comment.model.CommentDto;
import ru.yandex.practicum.shareIt.item.model.dto.IncomingItemDto;
import ru.yandex.practicum.shareIt.item.model.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {

    ItemDto createItem(long userId, @Valid IncomingItemDto incomingItemDto);

    CommentDto createComment(long userId, long itemId, CommentRequestDto commentRequestDto);

    ItemDto patchItem(ItemDto itemDto, long userId, long itemId);

    ItemDto getItemDto(long itemId, long userId);

    List<ItemDto> getItemsListFromUser(long userId, Long from, Long size);

    void deleteItem(long userId, long itemId);

    List<ItemDto> searchItem(String text, Long from, Long size);
}
