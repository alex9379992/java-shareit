package ru.yandex.practicum.shareIt.item;

import ru.yandex.practicum.shareIt.user.User;

import java.util.List;
import java.util.Map;

interface ItemStorage {
    ItemDto createItem(Item item, User user);

    ItemDto patchItem(ItemDto itemDto, int itemId);

    ItemDto getItem(int itemId);

    void deleteItem(int itemId);

    List<ItemDto> getItemsListFromUser(int userId);

    Map<Integer, Item> getItemsMap();

    List<ItemDto> searchItem(String text);
}
