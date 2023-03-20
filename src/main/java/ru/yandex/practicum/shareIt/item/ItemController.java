package ru.yandex.practicum.shareIt.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;


    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") int userUd, @Valid @RequestBody Item item) {
        log.info("Получен запрос на сохранение вещи от пользователя с id " + userUd);
        return itemService.createItem(userUd, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int userId,
                             @PathVariable int itemId) {
        log.info("Принят запрос на изменение вещи от пользователя" + userId);
        return itemService.patchItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        log.info("Получен запрос на вещь с id " + itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsListFromUser(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получен запрос на список вещей пользователя с id " + userId);
        return itemService.getItemsListFromUser(userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        log.info("Получен запрос на удаление вещи с id " + itemId + " пользоветелем с id " + userId);
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam String text) {
        log.info("Получен запрос на список доступных вещей с параметром " + text + " от пользоветеля с id " + userId);
        return itemService.searchItem(text);
    }
}
