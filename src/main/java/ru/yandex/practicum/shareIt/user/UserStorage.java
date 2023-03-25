package ru.yandex.practicum.shareIt.user;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    UserDto createUser(User user);
    UserDto patchUser(User user, int userId);
    UserDto getUser(int userId);
    void deleteUser(int userId);
    List<UserDto> getUsers();
    Map<Integer, User> getUsersMap();
}
