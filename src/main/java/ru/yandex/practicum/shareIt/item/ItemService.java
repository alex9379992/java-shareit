package ru.yandex.practicum.shareIt.item;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.shareIt.item.comment.model.CommentRequestDto;
import ru.yandex.practicum.shareIt.item.comment.model.CommentDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

interface ItemService {

    ItemDto createItem(long userId, @Valid ItemDto itemDto);

    CommentDto createComment(long userId, long itemId, CommentRequestDto commentRequestDto);

    ItemDto patchItem(ItemDto itemDto, int userId, long itemId);

    @Transactional(readOnly = true)
    ItemDto getItemDto(long itemId, long userId);

    @Transactional(readOnly = true)
    List<ItemDto> getItemsListFromUser(long userId);

    void deleteItem(int userId, long itemId);

    @Transactional(readOnly = true)
    List<ItemDto> searchItem(String text);
}
