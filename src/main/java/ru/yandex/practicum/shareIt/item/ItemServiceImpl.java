package ru.yandex.practicum.shareIt.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.shareIt.exeptions.SearchException;
import ru.yandex.practicum.shareIt.user.UserServiceImpl;
import ru.yandex.practicum.shareIt.user.UserValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Validated
@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    public final UserServiceImpl userService;

    @Override
    public ItemDto createItem(long userId, @Valid ItemDto itemDto) {
        if (UserValidator.isThereAUser(userId, userService.getUsersMap())) {
             Item item = itemRepository.save(ItemMapper.mapToItem(itemDto, userId));
            log.info("Новая вещь с id " + item.getId() + " сохранена");
            return ItemMapper.mapToItemDto(item);
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new SearchException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, int userId, long itemId) {
        if (UserValidator.isThereAUser(userId, userService.getUsersMap())) {
            if (ItemValidator.isThereAItem(itemId, getItemsMap())) {
                itemDto.setId(itemId);
                Item item = getItemById(itemId);
                if (getItemById(itemId).getOwner() == userId) {
                    Item itemAfterPatch = itemRepository.save(patcher(itemDto, item));
                    log.info("Данные о вещи с id " + itemId + " обновленны");
                    return ItemMapper.mapToItemDto(itemAfterPatch);
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

    @Override
    public ItemDto getItem(long itemId) {
        if (ItemValidator.isThereAItem(itemId, getItemsMap())) {
             Item item = getItemById(itemId);
                 log.info("Вещь с id " + itemId + " отправлена");
                 return ItemMapper.mapToItemDto(item);
        } else {
            log.warn("Вещь с id " + itemId + " не найдена");
            throw new SearchException("Вещь с id " + itemId + " не найдена");
        }
    }

    @Override
    public List<ItemDto> getItemsListFromUser(long userId) {
        if (UserValidator.isThereAUser(userId, userService.getUsersMap())) {
            log.info("Сформирован и отправлен список вещей пользователя с id " + userId);
            return itemRepository.findAllByOwner(userId).stream().map(ItemMapper:: mapToItemDto).collect(Collectors.toList());
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new SearchException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    public void deleteItem(int userId, long itemId) {
        if (UserValidator.isThereAUser(userId, userService.getUsersMap())) {
            if (ItemValidator.isThereAItem(itemId, getItemsMap())) {
                Item item = getItemById(itemId);
                if (item.getOwner() == userId) {
                    log.info("Вещь с id " + itemId + " удалена");
                    itemRepository.deleteById(itemId);
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

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            log.warn("Отправлен пустой список вещей, так как text пустой");
            return new ArrayList<>();
        }
        log.info("Сформирован и отправлен список вещей, подходящих под описание: " + text);
        return itemRepository.findByNameOrDescriptionContainingIgnoreCase(text, text)
                .stream().filter(Item:: getAvailable).map(ItemMapper::mapToItemDto).collect(Collectors.toList());
    }

    private Map<Long, Item> getItemsMap() {
        return  itemRepository.findAll().stream().collect(Collectors.toMap(Item :: getId, item -> item));
    }

    private Item getItemById(long id) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if(itemOptional.isPresent()) {
            return itemOptional.get();
        } else {
            log.warn("Обьекта не существует");
            throw new NullPointerException("Обьекта не существует");
        }
    }

    private Item patcher(ItemDto itemDto, Item item) {
        if(itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getOwner() != null) {
            item.setOwner(itemDto.getOwner());
        }
        if (itemDto.getRequest() != null) {
            item.setRequest(itemDto.getRequest());
        }
        return item;
    }
}
