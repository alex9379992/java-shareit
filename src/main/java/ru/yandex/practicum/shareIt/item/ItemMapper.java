package ru.yandex.practicum.shareIt.item;

public class ItemMapper {

    public static ItemDto getItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getOwner(), item.getRequest());
    }
}
