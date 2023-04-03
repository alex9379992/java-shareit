package ru.yandex.practicum.shareIt.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на сохранение вещи от пользователя с id " + userId);
        return ResponseEntity.ok().body(itemService.createItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> patchItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int userId,
                             @PathVariable int itemId) {
        log.info("Принят запрос на изменение вещи от пользователя " + userId);
        return ResponseEntity.ok().body(itemService.patchItem(itemDto, userId, itemId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable int itemId) {
        log.info("Получен запрос на вещь с id " + itemId);
        return ResponseEntity.ok().body(itemService.getItem(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsListFromUser(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получен запрос на список вещей пользователя с id " + userId);
        return ResponseEntity.ok().body(itemService.getItemsListFromUser(userId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        log.info("Получен запрос на удаление вещи с id " + itemId + " пользоветелем с id " + userId);
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam String text) {
        log.info("Получен запрос на список доступных вещей с параметром " + text + " от пользоветеля с id " + userId);
        return ResponseEntity.ok().body(itemService.searchItem(text));
    }
}
