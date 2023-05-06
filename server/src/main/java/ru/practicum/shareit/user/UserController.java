package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.dto.UserDto;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Принят запрос на сохранения пользователя");
        return ResponseEntity.ok().body(userService.createUser(userDto));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> patchUser(@RequestBody UserDto userDto, @PathVariable int userId) {
        log.info("Принят запрос на изменение пользователя " + userId);
        return ResponseEntity.ok().body(userService.patchUser(userDto, userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable int userId) {
        log.info("Принят запрос на получение пользователя " + userId);
        return ResponseEntity.ok().body(userService.getUserDto(userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        log.info("Принят запрос на удаление пользователя " + userId);
        userService.deleteUser(userId);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        log.info("Принят запрос на список пользователей");
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleSQLException(final SQLException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.CONFLICT);
    }
}
