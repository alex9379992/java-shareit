package ru.yandex.practicum.shareIt.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public UserDto createUser(@Valid @RequestBody User user) {
        log.info("Принят запрос на сохранения пользователя");
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@RequestBody User user,@PathVariable int userId){
        log.info("Принят запрос на изменение пользователя " + userId);
        return userService.patchUser(user, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable int userId){
        log.info("Принят запрос на получение пользователя " + userId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId){
        log.info("Принят запрос на удаление пользователя " + userId);
        userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Принят запрос на список пользователей");
        return userService.getUsers();
    }
}
