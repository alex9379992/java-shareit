package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.comment.model.CommentRequestDto;
import ru.practicum.shareit.item.model.dto.IncomingItemDto;
import ru.practicum.shareit.item.model.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;


    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody IncomingItemDto incomingItemDto) {
        log.info("Получен запрос на сохранение вещи от пользователя с id " + userId);
        return ResponseEntity.ok().body(itemService.createItem(userId, incomingItemDto));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                                    @Valid @RequestBody CommentRequestDto comment) {
        log.info("Получен запрос на сохранение вещи от пользователя с id " + userId);
        return ResponseEntity.ok().body(itemService.createComment(userId, itemId, comment));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> patchItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int userId,
                                             @PathVariable int itemId) {
        log.info("Принят запрос на изменение вещи от пользователя " + userId);
        return ResponseEntity.ok().body(itemService.patchItem(itemDto, userId, itemId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable("itemId") Long itemId,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на вещь с id " + itemId);
        return ResponseEntity.ok().body(itemService.getItemDto(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsListFromUser(@RequestHeader("X-Sharer-User-Id") int userId,
                                                              @RequestParam(required = false) Long from,
                                                              @RequestParam(required = false) Long size) {
        log.info("Получен запрос на список вещей пользователя с id " + userId);
        return ResponseEntity.ok().body(itemService.getItemsListFromUser(userId, from, size));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable int itemId) {
        log.info("Получен запрос на удаление вещи с id " + itemId + " пользоветелем с id " + userId);
        itemService.deleteItem(userId, itemId);
        return new ResponseEntity<Void>( HttpStatus.OK );
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                                    @RequestParam String text,
                                                    @RequestParam(required = false) Long from,
                                                    @RequestParam(required = false) Long size) {
        log.info("Получен запрос на список доступных вещей с параметром " + text + " от пользоветеля с id " + userId);
        return ResponseEntity.ok().body(itemService.searchItem(text, from, size));
    }
}
