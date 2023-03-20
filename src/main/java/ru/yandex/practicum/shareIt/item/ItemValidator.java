package ru.yandex.practicum.shareIt.item;

import java.util.Map;

public class ItemValidator {

    public static boolean isThereAItem (int itemId, Map<Integer, Item> itemsMap) {
        return itemsMap.containsKey(itemId);
    }
}
