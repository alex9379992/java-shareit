package ru.yandex.practicum.shareIt.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component("InMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> usersMap = new HashMap<>();
    private int id = 0;

    @Override
    public UserDto createUser(User user) {
        user.setId(generatedId());
        usersMap.put(user.getId(), user);
        log.info("Новый пользователь создан с id " + user.getId());
        return UserMapper.getUserDto(user);
    }

    @Override
    public UserDto patchUser(User user, int userId) {
        user.setId(userId);
        if (user.getName() != null) {
            usersMap.get(userId).setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (UserValidator.validationEmail(user, getUsersMap())) {
                usersMap.get(userId).setEmail(user.getEmail());
            }
        }
        log.info("Информация о пользователе с id " + userId + " обновлена");
        return UserMapper.getUserDto(usersMap.get(userId));
    }

    @Override
    public UserDto getUser(int userId) {
        log.info("Отправлен пользователь с id " + userId);
        return UserMapper.getUserDto(usersMap.get(userId));
    }

    @Override
    public void deleteUser(int userId) {
        log.info("Пользователь с id " + userId + " удален");
        usersMap.remove(userId);
    }

    @Override
    public List<UserDto> getUsers() {
        log.info("Сформирован и отправлен список пользователей");
        return usersMap.values().stream().map(UserMapper::getUserDto).collect(Collectors.toList());
    }

    @Override
    public Map<Integer, User> getUsersMap() {
        return usersMap;
    }

    private int generatedId() {
        return ++id;
    }
}
