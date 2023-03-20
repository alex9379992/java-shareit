package ru.yandex.practicum.shareIt.item;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Integer, Item> itemsMap = new HashMap<>();
    private int id = 0;

    @Override
    public ItemDto createItem(int id, Item item) {
        item.setId(generatedId());
        item.setOwner(id);
        itemsMap.put(item.getId(), item);
        return ItemMapper.getItemDto(item);
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, int itemId) {
        if (itemDto.getName() != null) {
            itemsMap.get(itemId).setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemsMap.get(itemId).setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemsMap.get(itemId).setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.getItemDto(itemsMap.get(itemId));
    }

    @Override
    public ItemDto getItem(int itemId) {
        return ItemMapper.getItemDto(itemsMap.get(itemId));
    }

    @Override
    public void deleteItem(int itemId) {
        itemsMap.remove(itemId);
    }

    @Override
    public List<ItemDto> getItemsListFromUser(int userId) {
        return itemsMap.values().stream().filter(item -> item.getOwner() == userId).
                map(ItemMapper::getItemDto).
                collect(Collectors.toList());
    }

    @Override
    public Map<Integer, Item> getItemsMap() {
        return itemsMap;
    }


    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemsMap.values().stream().filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase())
                        || item.getName().toLowerCase().contains(text.toLowerCase())).
                filter(Item::getAvailable).
                map(ItemMapper::getItemDto).
                collect(Collectors.toList());
    }

    private int generatedId() {
        return ++id;
    }
}