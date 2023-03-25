package ru.yandex.practicum.shareIt.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.shareIt.exeptions.SearchException;
import ru.yandex.practicum.shareIt.exeptions.ValidationException;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(User user) {
        if (UserValidator.validation(user, userStorage.getUsersMap())) {
            return userStorage.createUser(user);
        } else {
            log.warn("Ошибка валидации");
            throw new ValidationException("Ошибка валидации");
        }

    }

    public UserDto patchUser(User user, int userId) {
        if (UserValidator.isThereAUser(userId, userStorage.getUsersMap())) {
            return userStorage.patchUser(user, userId);
        } else {
            log.warn("Пользователь " + userId + " не найден");
            throw new SearchException("Пользователь " + userId + " не найден");
        }
    }

    public UserDto getUser(int userId) {
        if (UserValidator.isThereAUser(userId, userStorage.getUsersMap())) {
            return userStorage.getUser(userId);
        } else {
            log.warn("Пользователь " + userId + " не найден");
            throw new SearchException("Пользователь " + userId + " не найден");
        }
    }

    public void deleteUser(int userId) {
        if (UserValidator.isThereAUser(userId, userStorage.getUsersMap())) {
            userStorage.deleteUser(userId);
        } else {
            log.warn("Пользователь " + userId + " не найден");
            throw new SearchException("Пользователь " + userId + " не найден");
        }
    }

    public List<UserDto> getUsers() {
        return userStorage.getUsers();
    }
}
