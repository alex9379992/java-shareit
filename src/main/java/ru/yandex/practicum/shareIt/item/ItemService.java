package ru.yandex.practicum.shareIt.item;

import ru.yandex.practicum.shareIt.comment.model.CommentRequestDto;
import ru.yandex.practicum.shareIt.comment.model.CommentDto;
import ru.yandex.practicum.shareIt.item.model.IncomingItem;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.item.model.ItemDto;
import ru.yandex.practicum.shareIt.user.model.User;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {

    ItemDto createItem(long userId, @Valid IncomingItem incomingItem);

    CommentDto createComment(long userId, long itemId, CommentRequestDto commentRequestDto);

    ItemDto patchItem(ItemDto itemDto, long userId, long itemId);

    ItemDto getItemDto(long itemId, long userId);

    Item findItemById(long id);

    List<Item> findAllByOwner(User owner);
    List<Item> findAllByRequestId(long requestId);

    List<ItemDto> getItemsListFromUser(long userId, Long from, Long size);

    void deleteItem(int userId, long itemId);

    List<ItemDto> searchItem(String text, Long from, Long size);
}
