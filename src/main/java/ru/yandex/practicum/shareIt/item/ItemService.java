package ru.yandex.practicum.shareIt.item;

import ru.yandex.practicum.shareIt.item.comment.model.CommentRequestDto;
import ru.yandex.practicum.shareIt.item.comment.model.CommentDto;
import ru.yandex.practicum.shareIt.item.model.ItemDto;

import javax.validation.Valid;
import java.util.List;

interface ItemService {

    ItemDto createItem(long userId, @Valid ItemDto itemDto);

    CommentDto createComment(long userId, long itemId, CommentRequestDto commentRequestDto);

    ItemDto patchItem(ItemDto itemDto, int userId, long itemId);

    ItemDto getItemDto(long itemId, long userId);

    List<ItemDto> getItemsListFromUser(long userId);

    void deleteItem(int userId, long itemId);

    List<ItemDto> searchItem(String text);
}
