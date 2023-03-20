package ru.yandex.practicum.shareIt.item;

import java.util.List;
import java.util.Map;

interface ItemStorage {
    ItemDto createItem(int id, Item item);

    ItemDto patchItem(ItemDto itemDto, int itemId);

    ItemDto getItem(int itemId);

    void deleteItem(int itemId);

    List<ItemDto> getItemsListFromUser(int userId);

    Map<Integer, Item> getItemsMap();

    List<ItemDto> searchItem(String text);
}
