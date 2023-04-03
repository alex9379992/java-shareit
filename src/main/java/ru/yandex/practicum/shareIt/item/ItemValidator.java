package ru.yandex.practicum.shareIt.item;

import java.util.Map;

public class ItemValidator {

    public static boolean isThereAItem (long itemId, Map<Long, Item> itemsMap) {
        return itemsMap.containsKey(itemId);
    }
}
