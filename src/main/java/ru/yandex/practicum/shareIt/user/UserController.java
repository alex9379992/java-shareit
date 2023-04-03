package ru.yandex.practicum.shareIt.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    public ResponseEntity<UserDto> patchUser(@RequestBody UserDto userDto,@PathVariable int userId){
        log.info("Принят запрос на изменение пользователя " + userId);
        return ResponseEntity.ok().body(userService.patchUser(userDto, userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable int userId){
        log.info("Принят запрос на получение пользователя " + userId);
        return ResponseEntity.ok().body(userService.getUser(userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId){
        log.info("Принят запрос на удаление пользователя " + userId);
        userService.deleteUser(userId);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        log.info("Принят запрос на список пользователей");
        return ResponseEntity.ok().body(userService.getUsers());
    }
}
