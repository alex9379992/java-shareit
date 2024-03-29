package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.dto.IncomingRequestDto;
import ru.practicum.shareit.request.model.dto.RequestDto;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/requests")
@Slf4j
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestDto> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @Valid @RequestBody IncomingRequestDto incomingRequestDto) {
        log.info("Получен запрос на сохранение запроса вещи от пользователя с id " + userId);
        return ResponseEntity.ok().body(requestService.createRequest(incomingRequestDto, userId));
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> findRequestDtoListFromUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на список запросов вещей от пользователя с id " + userId);
        return ResponseEntity.ok().body(requestService.findRequestDtoListFromUser(userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDto> findRequestDtoFromId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @PathVariable("requestId") Long requestId) {
        log.info("Получен запрос на запрос вещи с id " + requestId);
        return ResponseEntity.ok().body(requestService.findRequestDtoFromId(requestId, userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDto>> findAllRequestDtoList(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                  @RequestParam(required = false) Long from,
                                                                  @RequestParam(required = false) Long size) {
        log.info("Получен запрос на список запросов с пагинацией от пользователя id " + userId);
        return ResponseEntity.ok().body(requestService.findAllRequestDtoListFromPagination(userId, from, size));
    }
}
