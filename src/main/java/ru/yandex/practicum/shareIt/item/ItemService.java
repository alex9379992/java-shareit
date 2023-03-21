package ru.yandex.practicum.shareIt.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.shareIt.exeptions.SearchException;
import ru.yandex.practicum.shareIt.user.UserStorage;
import ru.yandex.practicum.shareIt.user.UserValidator;

import javax.validation.Valid;
import java.util.List;


@Validated
@Service
@Slf4j
public class ItemService {
    private final ItemStorage itemStorage;
    public final UserStorage userStorage;

    public ItemService(@Qualifier("InMemoryItemStorage") ItemStorage itemStorage,
                           @Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public ItemDto createItem(int userId, @Valid Item item) {
        if (UserValidator.isThereAUser(userId, userStorage.getUsersMap())) {
            return itemStorage.createItem(item, userStorage.getUsersMap().get(userId));
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new SearchException("Пользователь с id " + userId + " не найден");
        }
    }

    public ItemDto patchItem(ItemDto itemDto, int userId, int itemId) {
        if (UserValidator.isThereAUser(userId, userStorage.getUsersMap())) {
            if (ItemValidator.isThereAItem(itemId, itemStorage.getItemsMap())) {
                if (itemStorage.getItem(itemId).getOwner().getId() == userId) {
                    return itemStorage.patchItem(itemDto, itemId);
                } else {
                    log.warn("У вещи с id " + itemId + " не соответствует владелец с id " + userId);
                    throw new SearchException("У вещи с id " + itemId + " не соответствует владелец с id " + userId);
                }
            } else {
                log.warn("Вещь с id " + itemId + " не найдена");
                throw new SearchException("Вещь с id " + itemId + " не найдена");
            }
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new SearchException("Пользователь с id " + userId + " не найден");
        }
    }

    public ItemDto getItem(int itemId) {
        if (ItemValidator.isThereAItem(itemId, itemStorage.getItemsMap())) {
            return itemStorage.getItem(itemId);
        } else {
            log.warn("Вещь с id " + itemId + " не найдена");
            throw new SearchException("Вещь с id " + itemId + " не найдена");
        }
    }

    public List<ItemDto> getItemsListFromUser(int userId) {
        if (UserValidator.isThereAUser(userId, userStorage.getUsersMap())) {
            return itemStorage.getItemsListFromUser(userId);
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new SearchException("Пользователь с id " + userId + " не найден");
        }
    }

    public void deleteItem(int userId, int itemId) {
        if (UserValidator.isThereAUser(userId, userStorage.getUsersMap())) {
            if (ItemValidator.isThereAItem(itemId, itemStorage.getItemsMap())) {
                if (itemStorage.getItem(itemId).getOwner().getId() == userId) {
                    itemStorage.deleteItem(itemId);
                } else {
                    log.warn("У вещи с id " + itemId + " не соответствует владелец с id " + userId);
                    throw new SearchException("У вещи с id " + itemId + " не соответствует владелец с id " + userId);
                }
            } else {
                log.warn("Вещь с id " + itemId + " не найдена");
                throw new SearchException("Вещь с id " + itemId + " не найдена");
            }
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new SearchException("Пользователь с id " + userId + " не найден");
        }
    }

    public List<ItemDto> searchItem(String text) {
        return itemStorage.searchItem(text);
    }
}
