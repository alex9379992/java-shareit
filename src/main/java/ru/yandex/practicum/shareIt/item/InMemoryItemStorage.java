package ru.yandex.practicum.shareIt.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.shareIt.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("InMemoryItemStorage")
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Integer, Item> itemsMap = new HashMap<>();
    private int id = 0;

    @Override
    public ItemDto createItem(Item item, User user) {
        item.setId(generatedId());
        item.setOwner(user);
        itemsMap.put(item.getId(), item);
        log.info("Новая вещь с id " + item.getId() + " сохранена");
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
        log.info("Данные о вещи с id " + itemId + " обновленны");
        return ItemMapper.getItemDto(itemsMap.get(itemId));
    }

    @Override
    public ItemDto getItem(int itemId) {
        log.info("Вещь с id " + itemId + " отправлена");
        return ItemMapper.getItemDto(itemsMap.get(itemId));
    }

    @Override
    public void deleteItem(int itemId) {
        log.info("Вещь с id " + itemId + " удалена");
        itemsMap.remove(itemId);
    }

    @Override
    public List<ItemDto> getItemsListFromUser(int userId) {
        log.info("Сформирован и отправлен список вещей пользователя с id " + userId);
        return itemsMap.values().stream().filter(item -> item.getOwner().getId() == userId).
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
            log.warn("Отправлен пустой список вещей, так как text пустой");
            return new ArrayList<>();
        }
        log.info("Сформирован и отправлен список вещей, подходящих под описание: " + text);
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