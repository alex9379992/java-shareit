package ru.yandex.practicum.shareIt.item;

import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

interface ItemService {

    ItemDto createItem(long userId, @Valid ItemDto itemDto);
    ItemDto patchItem(ItemDto itemDto, int userId, long itemId);
    @Transactional(readOnly = true)
    ItemDto getItem(long itemId);
    @Transactional(readOnly = true)
    List<ItemDto> getItemsListFromUser(long userId);
    void deleteItem(int userId, long itemId);
    @Transactional(readOnly = true)
    List<ItemDto> searchItem(String text);

}
